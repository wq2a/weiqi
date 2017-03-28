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
from lxml import html
from lxml import etree
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
            print './parser.py --dbhost cpm01lt.hs.it.vumc.io --dbuser cpmds --dbpass pass --dbname spl --dataset otc --poolsize 6'
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

    da = dataAll(options)
    length = len(da)
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
            p = Process(target=f, args=(i, da[start:],options,))
        else:
            p = Process(target=f, args=(i, da[start:end],options,))
        p.start()
        procs.append(p)
        print i,'started!'


    for p in procs:
        p.join()

    print 'Bye!'
    exit(0)

def f(pid, da, opts):
    db = MySQLdb.connect(opts['dbhost'],opts['dbuser'],opts['dbpass'],opts['dbname'])
    cursor = db.cursor()

    sql = 'update '+opts['dataset']+'_info set property_all=%s, content_all=%s where id=%s'

    for data in da:
        id = data[0]
        propertyAll = mytrim(data[1])
        contentAll = mytrim(data[2])
        cursor.execute(sql, (propertyAll,contentAll,id))
        db.commit()
    db.close()
    exit(0)

def dataAll(opts):
    db = MySQLdb.connect(opts['dbhost'],opts['dbuser'],opts['dbpass'],opts['dbname'])
    cursor = db.cursor()
    sql = 'select id, property_all, content_all from '+opts['dataset']+'_info'
    cursor.execute(sql)
    results = cursor.fetchall()
    db.close()
    return results
  

def mytrim(s):
    temp = re.sub(' +', ' ', s)
    temp = re.sub('\n ', '\n', temp)
    temp = re.sub('\n+', '\n', temp)
    temp = temp.replace('\xc2\xa0', ' ')
    return temp.strip(' \t\r\n\0')

if __name__ == '__main__':
    main(sys.argv[1:])
