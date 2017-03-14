
import MySQLdb
import re 
import urllib2
import lxml
from lxml import html

def mysqlformat(value):
    temp = re.sub(r'"','',value)
    return re.sub(r"'",'',temp)

def insertValue(cursor, data):
    if 'link' not in data:
        data['link'] = ''
    sql = 'insert into wiki_icd910_info_value (entity_id, property_id, hierarchy, value,link) values ("%s","%s","%s","%s","%s")' % (data['eid'],data['pid'],data['hi'],mysqlformat(data['value']), data['link'])
    cursor.execute(sql)

# remove extra characters
def pfilter(propertyName):
    return re.sub(r'\[edit\]','',propertyName)


def insertProperty(cursor, propertyName):
    propertyName = pfilter(propertyName)
    pid = getPropertyID(cursor, propertyName)
    if pid:
        return pid
    else:
        sql = 'insert into wiki_icd910_info_property (name) values ("%s")' % (propertyName)
        cursor.execute(sql)
        return cursor.lastrowid

def getPropertyID(cursor, propertyName):
    propertyName = pfilter(propertyName)
    sql = 'select id from wiki_icd910_info_property where name = "%s"' % (propertyName)

    cursor.execute(sql)
    row = cursor.fetchone()
    if row:
        return row[0]
    else:
        return None

def process(db,cursor, entityID, link):

    content = urllib2.urlopen(link).read()
    tree = lxml.html.fromstring(content)

    firstHeading = tree.body.get_element_by_id('content').get_element_by_id('firstHeading')
    #print firstHeading.text_content()
    mwContent = tree.body.get_element_by_id('content').get_element_by_id('bodyContent').get_element_by_id('mw-content-text').getchildren()

    hierarchy = []
    level = 0
    data = {}

    for child in mwContent:
        if isinstance(child.tag,basestring):
            #print child.tag
            if 'h' in '' + child.tag:
                if 'references' in child.text_content().lower():
                    print 'STOP hit reference!'
                    break
                if 'value' in data and len(data['value'])>0:
                    data['eid'] = entityID
                    if len(hierarchy)<1:
                        data['pid'] = insertProperty(cursor,'wiki_abstract')
                    else:
                        data['pid'] = insertProperty(cursor,hierarchy[level])
                    data['hi'] = ''
                    for i in range(0, level):
                        data['hi'] += str(insertProperty(cursor,hierarchy[i])) + ','
                    insertValue(cursor, data)
                    db.commit()
                    data = {}
                
                s = re.findall('\d+', child.tag)
                level = int(s[0]) - 2
                if level == len(hierarchy):
                    hierarchy.append(child.text_content())
                else:
                    hierarchy[level] = child.text_content() 
            # get Classification and external sources
            elif child.tag == 'table' and 'wikitable' not in child.classes and len(hierarchy)==0:
                trs = child.findall('tr')
                for tr in trs:
                    thd = tr.getchildren();
                    if len(thd) > 1:
                        print 'class: ' + thd[0].text_content()
                        print 'id: ' + thd[1].text_content()
                        #print 'urls: ' + thd[1].xpath('a/@href')[0]
                        pname = thd[0].text_content()
                        value = thd[1].text_content()
                        #valuelink = thd[1].xpath('a/@href')[0]
                        data['eid'] = entityID
                        data['pid'] = insertProperty(cursor,pname)
                        data['value'] = value
                        data['hi'] = ''
                        ls = thd[1].xpath('a/@href')
                        if len(ls) > 0:
                            data['link'] = ls[0]
                        insertValue(cursor, data)
                        db.commit()
                        data = {}
            elif 'p' in '' + child.tag or 'ul' in child.tag or 'table' in child.tag:
                if 'value' not in data:
                    data['value'] = ''
                data['value'] += child.text_content()

def main():

    db = MySQLdb.connect("localhost","root","","umls_2016ab" )
    
    cursor = db.cursor()
    
    sql = "select id, link from wiki_icd910_info_entity"
    
    cursor.execute(sql)
    results = cursor.fetchall()
    for row in results:
        try:
            entityID = row[0]
            link = row[1]
            process(db,cursor, entityID, link)
        except:
            print "Error"
    db.commit()
    db.close()
    
    
main()    
