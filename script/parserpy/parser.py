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

from multiprocessing import Pool
from multiprocessing import Process
from lxml import html
from lxml import etree
from StringIO import StringIO
from itertools import chain

def main(argv):
    options = {'dbhost':'localhost', 'dbuser':'root', 'dbpass':'', 'dbname':'spl', 'dataset':'', 'poolsize':8}

    try:
        opts, args = getopt.getopt(argv, "h", [opt+'=' for opt in options.keys()])
    except getopt.GetoptError:
        print 'Error'
        sys.exit(2)
    for opt, arg in opts:
        if opt in ('-h'):
            print './parser.py --dbhost locahost --dbuser root --dbpass pass --dataset rxnorm'
        elif opt[2:] in options.keys():
            options[opt[2:]] = arg
        else:
            print opt, 'option not allowed'
            sys.exit(2)
 
    fList = os.listdir('../data')
    length = len(fList)
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
            p = Process(target=f, args=(i, fList[start:],options,))
        else:
            p = Process(target=f, args=(i, fList[start:end],options,))
        p.start()
        procs.append(p)

    print 'wait all the processes to finish'

    for p in procs:
        p.join()

    print 'done!'

def f(pid, fl, opts):
    db = MySQLdb.connect(opts['dbhost'],opts['dbuser'],opts['dbpass'],opts['dbname'])
    cursor = db.cursor()
    for f in fl:
        if '.zip' in f:
            dirP = '../data/'+f.replace('.zip','')
            os.system('mkdir '+dirP)
            os.system('cp ../data/'+f+' '+dirP+'/')
            os.system('tar -zxf '+dirP+'/'+f + ' -C '+dirP)
            fns = os.listdir(dirP+'/')
            for fn in fns:
                if '.xml' in fn:
                    fo = open(dirP+'/'+fn,'r')
                    xml = fo.read()
                    fo.close()
                    tree = etree.parse(StringIO(xml))
                    doc = tree.getroot()

                    data = {'entityID':'','entityName':'','code':'','sabCode':'','sab':'', 'effectiveTime':''}

                    effectiveTime = ''
                    for el in doc:
                        attrs = el.attrib
                        if tag(el, 'id'):
                            data['entityID'] = attrs['root']
                        elif tag(el, 'code'):
                            data['code'] = attrs['code']
                            data['sabCode'] = attrs['codeSystem']
                            data['sab'] = attrs['displayName']
                        elif tag(el, 'title'):
                            data['entityName'] = el.text
                            if not data['entityName']:
                                attt = el.attrib
                                if 'value' in attt:
                                    data['entityName'] = attt['value']
                            if not data['entityName']:
                                data['entityName'] = ''
   
                        elif tag(el, 'effectiveTime'):
                            data['effectiveTime'] = attrs['value']
                        elif tag(el, 'component'):
                            for subel in el:
                                if tag(subel, 'structuredBody'):
                                    for e in subel:
                                        if tag(e, 'component'):
                                            component(cursor, opts, data['entityID'],e,False)
                    #TODO insert into entity table
                    try:
                        insertEntity(cursor, data, opts)
                    except:
                        print 'ERROR', data
                    db.commit()
 
    db.close()


def tag(el,tag):
    return realTag(el)==tag

def realTag(el):
    return re.sub(r'\{.*\}','',el.tag)
    
def stringfy(el):
    if len(el.getchildren())==0 and el.text:
        return el.text
    parts = ([el.text] + list(chain(*([c.text, re.sub(r'<[^>]*>|\[[^\]]*\]','',etree.tostring(c)).strip('\t\n\r'),c.tail] for c in el.getchildren())))+[el.tail])
    return ''.join(filter(None, parts))

def component(cursor,opts, entityID,comp,flag):
    pdata = {'id':'','name':'','code':'','sabCode':'','sab':''}
    vdata = {'entityID':entityID,'propertyID':'','value':''}
    for el in comp:
        if tag(el, 'section'):
            for e in el:
                if tag(e, 'code'):
                    attrs = e.attrib
                    if not flag and attrs['code'] != '34067-9':
                        break
                    else:
                        pdata['code'] = attrs['code']
                        pdata['sabCode'] = attrs['codeSystem']
                        pdata['sab'] = attrs['displayName']
                        flag = True
                elif tag(e, 'component'):
                    # recursive
                    component(cursor, opts, entityID, e, flag)
                elif tag(e, 'id'):
                    # ignored
                    ddd = ''
                elif tag(e, 'title'):
                    pdata['name'] = e.text
                else:
                    vdata['value'] += stringfy(e)
    if flag:
        #print '+++',code,'+',sab, '+', sabCode, '+', propertyName,'===', value
        # TODO insert into property and value table
        # propertyid hash of all
        if not pdata['name']:
            pdata['name'] = ''
        try:
            pdata['id'] = hash(pdata['name']+pdata['code']+pdata['sabCode'])
            vdata['propertyID'] = pdata['id']
            insertProperty(cursor, pdata, opts)
            insertValue(cursor, vdata, opts)
        except:
            print 'ERROR', pdata, vdata
def insertEntity(cursor, data, opts):
    sql = 'insert into '+opts['dataset']+'_info_entity (id, name, code, sabCode, sab, effectivetime) values ("%s","%s","%s","%s","%s","%s")' % (data['entityID'],mysqlformat(data['entityName']),data['code'],data['sabCode'], mysqlformat(data['sab']), data['effectiveTime'])
    cursor.execute(sql)

def insertProperty(cursor, data, opts):
    sql = "select id from "+opts['dataset']+"_info_property where id = '"+str(data['id'])+"'"
    cursor.execute(sql)
    result = cursor.fetchone()
    if not result:
        sql = 'insert into '+opts['dataset']+'_info_property (id, name, code, sabCode, sab) values ("%s","%s","%s","%s","%s")' % (data['id'],mysqlformat(data['name']),data['code'],data['sabCode'], mysqlformat(data['sab']))
        cursor.execute(sql)

def insertValue(cursor, data, opts):
    sql = 'insert into '+opts['dataset']+'_info_value (entity_id, property_id,value) values ("%s","%s","%s")' % (data['entityID'],data['propertyID'],mysqlformat(data['value']))
    cursor.execute(sql)
 
def mysqlformat(value):
    temp = re.sub(r'"','',value)
    return re.sub(r"'",'',temp)

if __name__ == '__main__':
    main(sys.argv[1:])
