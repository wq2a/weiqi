#!/usr/bin/env python
#
import MySQLdb
import urllib2
import math
import time
import re 
import lxml
import sys
import os
import md5
import getopt
import signal

from multiprocessing import Pool, Process, cpu_count
from lxml import html, etree
from StringIO import StringIO
from itertools import chain

def main(argv):
    options = {'dbhost':'localhost', 'dbuser':'root', 'dbpass':'', 'dbname':'', 'dataset':'', 'poolsize':7}

    try:
        opts, args = getopt.getopt(argv, "h", [opt+'=' for opt in options.keys()])
    except getopt.GetoptError:
        print 'Error'
        sys.exit(2)
    for opt, arg in opts:
        if opt in ('-h'):
            print './parser.py --dbhost locahost --dbuser root --dbpass pass --dbname spl --dataset otc --poolsize 6'
        elif opt[2:] in options.keys():
            options[opt[2:]] = arg
        else:
            print opt, 'option not allowed'
            sys.exit(2)

    # check options
    for key, value in options.iteritems():
        if value=='' and key!='dbpass':
            print key, 'is required'
            sys.exit(2)

    if options['poolsize'] > cpu_count():
        print 'poolsize is >', cpu_count()
        sys.exit(2)


    dataAll = fetchAll(options)
    length = len(dataAll)
    poolsize = int(options['poolsize'])
    blocksize = length//poolsize
    procs = []

    if blocksize == 0:
        blocksize = 1

    for i in range(poolsize):
        start = i*blocksize
        end = start+blocksize
        if start >= length:
            break
        if i == poolsize-1:
            p = Process(target=f, args=(i, dataAll[start:],options,))
        else:
            p = Process(target=f, args=(i, dataAll[start:end],options,))
        p.start()
        procs.append(p)
        print i,'started!'

    for p in procs:
        p.join()

    print 'Bye!'
    exit(0)

def f(pid, da, opts):
    if pid == 1:
        time.sleep(5)
    db = MySQLdb.connect(opts['dbhost'],opts['dbuser'],opts['dbpass'],opts['dbname'])
    cursor = db.cursor()
    for item in da:
        entityID = item[0]
        link = item[1]
        if link and 'https://medlineplus.gov' not in link:
            if 'https://ghr.nlm.nih.gov' in link:
                tree = html.fromstring(fetchUrl(link))
                #head = tree.xpath('/html/body/main/div/div/div/section/h1')
                #entityName = head[0].text_content()
                r = tree.xpath('/html/body/main/div/div/div/section/div')
                if r and len(r) >= 2:
                    sections = r[1].xpath('section')
                    print sections[0].tag
                    pdata = {}
                    vdata = {}
                    for sec in sections:
                        div = sec.xpath('div')
                        pdata['brief'] = div[0].text_content().strip()
                        pdata['name'] = pdata['brief'].replace(' ','_')
                        vdata['entityID'] = entityID
                        vdata['propertyID'] = insertProperty(cursor, pdata, opts)
                        vdata['value'] = div[1].text_content()
                        insertValue(cursor, vdata, opts)
                        db.commit()
    db.close()
    exit(0)

# get links
def fetchAll(opts):
    db = MySQLdb.connect(opts['dbhost'],opts['dbuser'],opts['dbpass'],opts['dbname'])
    cursor = db.cursor()
    sql = 'select id, link from '+opts['dataset']+'_info_entity'
    cursor.execute(sql)
    results = cursor.fetchall()
    db.close()
    return results

def fetchUrl(link):
    return urllib2.urlopen(link).read()

def insertProperty(cursor, data, opts):
    sql = "select id from "+opts['dataset']+"_info_property where name = '"+str(data['name'])+"'"
    cursor.execute(sql)
    result = cursor.fetchone()
    if not result:
        sql = 'insert into '+opts['dataset']+'_info_property (name, brief) values ("%s","%s")' % (mysqlformat(data['name']),data['brief'])
        cursor.execute(sql)
    sql = "select id from "+opts['dataset']+"_info_property where name = '"+str(data['name'])+"'"
    cursor.execute(sql)
    result = cursor.fetchone()
    if result:
        return result[0]

def insertValue(cursor, data, opts):
    sql = 'insert into '+opts['dataset']+'_info_value (entity_id, property_id,value) values ("%s","%s","%s")' % (data['entityID'],data['propertyID'],mysqlformat(data['value']))
    cursor.execute(sql)
 
def tag(el,tag):
    return realTag(el)==tag

def realTag(el):
    return re.sub(r'\{.*\}','',el.tag)
    
def stringfy(el):
    if len(el.getchildren())==0 and el.text:
        return el.text
    parts = ([el.text] + list(chain(*([c.text, re.sub(r'<[^>]*>|\[[^\]]*\]','',etree.tostring(c)).strip('\t\n\r'),c.tail] for c in el.getchildren())))+[el.tail])
    return ''.join(filter(None, parts))

def mysqlformat(value):
    temp = re.sub(r'"','',value)
    return re.sub(r"'",'',temp)

if __name__ == '__main__':
    main(sys.argv[1:])
