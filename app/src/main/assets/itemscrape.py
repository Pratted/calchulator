import csv
import requests
import csv
import re
from bs4 import BeautifulSoup

items = []
with open('items.csv', 'rb') as infile:
    reader = csv.reader(infile, delimiter=",")
    
    for row in reader:
        items.append(row)

for item in items:
    try:
        page = requests.get("http://oldschoolrunescape.wikia.com/wiki/%s" % (item[1].replace(' ' , '_')))
        soup = BeautifulSoup(page.content, "html.parser")
        s = soup.find_all("div", {"class": "infobox-wrapper"})
        rows = soup.findChildren(['tr'])
        
        print item[1]

        alch = 'NA'
        limit = 'NA'

        for row in rows:
            if "high alch" in str(row).lower():
                d = row.findChildren('td')
                #print type(d[0].text.encode('ascii', 'replace'))
                #print d[0].text.replace(',', '').replace('coins', '').strip()
                alch = re.findall(r'\d+',d[0].text.encode('ascii', 'replace').replace(',', '').replace('coins', '').strip())[0]
                print alch

            if "buy limit" in str(row).lower():
                d = row.findChildren('td')
                #print d[0].text.strip()
                limit = d[0].text.encode('ascii', 'replace').replace(',', '').replace('Unknown', 'NA').strip()
                #limit = re.findall(r'\d+',d[0].text.encode('ascii', 'replace').replace(',', '').replace('coins', '').strip())[0]
                print limit

        item.append(alch)
        item.append(limit)
        print item
        with open('items3.csv', 'a') as outfile:
            writer = csv.writer(outfile, delimiter=',')
            writer.writerow(item)

    except KeyboardInterrupt:
        exit()
    except:
        print "Failed to scrape %s" % item[1]


#print(s)
