#!/usr/bin/env python
#
import MySQLdb
import time
import re 
import sys
import os
import getopt
import signal

from multiprocessing import Pool, Process, cpu_count

def main(argv):
    options = {'dbhost':'localhost', 'dbuser':'root', 'dbpass':'', 'dbname':'', 'prefix':'', 'poolsize':7, 'data':'../../../data'}

    try:
        opts, args = getopt.getopt(argv, "h", [opt+'=' for opt in options.keys()])
    except getopt.GetoptError:
        print 'Error'
        sys.exit(2)
    for opt, arg in opts:
        if opt in ('-h'):
            print './parser.py --dbhost locahost --dbuser root --dbpass pass --dbname wanjiang_semrep --poolsize 6'
        elif opt[2:] in options.keys():
            options[opt[2:]] = arg
        else:
            print opt, 'option not allowed'
            sys.exit(2)

    # check options
    for key, value in options.iteritems():
        if value=='' and key!='dbpass' and key!='prefix':
            print key, 'is required'
            sys.exit(2)

    if options['poolsize'] > cpu_count():
        print 'poolsize is >', cpu_count()
        sys.exit(2)

    # check data root dir, there's dir named 
    if not os.path.isdir(options['data']):
        print 'Error no data dir in', options['data']
        sys.exit(2)

    fList = os.listdir(options['data'])
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
        print i,'started!'

    print 'wait all the processes to finish'

    for p in procs:
        p.join()

    print 'Bye!'
    exit(1)

def f(pid, fl, opts):
    db = MySQLdb.connect(opts['dbhost'],opts['dbuser'],opts['dbpass'],opts['dbname'])
    cursor = db.cursor()
    for f in fl:
        print f
        fo = open(opts['data'] + '/' + f, 'r')
        sentenceID = 0
        for l in fo:
            if len(l)==0 or l[0] == '<':
                break
            data = l.split('|')
            if data[0] == 'SE':

                
                pmid = data[1]
                subsection = data[2]
                sentenceType = data[3]
                #sentenceID = data[4]
                category = data[5]
                if category == 'text':
                    sql = 'insert into ' + opts['prefix'] + 'SENTENCE (PMID,TYPE,SENTENCE) values ("%s","%s","%s")' % (data[1],data[3],data[6])
                    cursor.execute(sql)
                    db.commit()
                    sentenceID = cursor.lastrowid
                    sentence = data[6]
                elif category == 'entity':
                    sql = 'insert into ' + opts['prefix'] + 'ENTITY (SENTENCE_ID,CUI,NAME,SEMTYPE,GENE_ID,GENE_NAME,TEXT,SCORE,START_INDEX,END_INDEX) values ("%s","%s","%s","%s","%s","%s","%s","%s","%s","%s")' % (sentenceID,data[6],data[7],data[8],data[9],data[10],data[11],data[15],data[16],data[17])
                    cursor.execute(sql)
                    db.commit()
                    cui = data[6]
                    preferredName = data[7]
                    semType = data[8]
                    geneID = data[9]
                    geneName = data[10]
                    word = data[11]
                    changeTerm = data[12]
                    degreeTerm = data[13]
                    negationTerm = data[14]
                    confidenceScore = data[15]
                    fcp = data[16]
                    lcp = data[17]
                elif category == 'relation':
                    sql = 'insert into ' + opts['prefix'] + 'PREDICATION (SENTENCE_ID,PMID,PREDICATE,SUBJECT_CUI,SUBJECT_NAME,SUBJECT_SEMTYPE,OBJECT_CUI,OBJECT_NAME,OBJECT_SEMTYPE) values ("%s","%s","%s","%s","%s","%s","%s","%s","%s")' % (sentenceID,data[1],data[22],data[8],data[9],data[11],data[28],data[29],data[31])
                    cursor.execute(sql)
                    db.commit()
                    predicationID = cursor.lastrowid
                    sql = 'insert into ' + opts['prefix'] + 'PREDICATION_AUX (PREDICATION_ID,SUBJECT_TEXT,SUBJECT_DIST,SUBJECT_MAXDIST,SUBJECT_START_INDEX,SUBJECT_END_INDEX,SUBJECT_SCORE,INDICATOR_TYPE,PREDICATE_START_INDEX,PREDICATE_END_INDEX,OBJECT_TEXT,OBJECT_DIST,OBJECT_MAXDIST,OBJECT_START_INDEX,OBJECT_END_INDEX,OBJECT_SCORE) values ("%s","%s","%s","%s","%s","%s","%s","%s","%s","%s","%s","%s","%s","%s","%s","%s")' % (predicationID,data[14],data[7],data[6],data[19],data[20],data[18],data[21],data[24],data[25],data[34],data[27],data[26],data[39],data[40],data[38])
                    cursor.execute(sql)
                    db.commit()


                    subjectMaxDist = data[6]
                    subjectDist = data[7]
                    scui= data[8]
                    sPreferredName= data[9]
                    sSemTypes = data[10]
                    sSemType = data[11]
                    sGeneID = data[12]
                    sGeneName = data[13]
                    sText = data[14]
                    sChangeTerm = data[15]
                    sDegreeTerm = data[16]
                    sNegationTerm = data[17]
                    sConfidenceScore = data[18]
                    sfcp = data[19]
                    slcp = data[20]
                    indicatorType = data[21]
                    predicate = data[22]
                    rNegationTerm = data[23]
                    rfcp = data[24]
                    rlcp = data[25]

                    oubjectMaxDist = data[26]
                    oubjectDist = data[27]
                    ocui= data[28]
                    oPreferredName= data[29]
                    oSemTypes = data[30]
                    oSemType = data[31]
                    oGeneID = data[32]
                    oGeneName = data[33]
                    oText = data[34]
                    oChangeTerm = data[35]
                    oDegreeTerm = data[36]
                    oNegationTerm = data[37]
                    oConfidenceScore = data[38]
                    ofcp = data[39]
                    olcp = data[40]

            print l
    db.close()
    exit(1)

if __name__ == '__main__':
    main(sys.argv[1:])
