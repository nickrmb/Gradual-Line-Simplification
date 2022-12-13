import requests
import re
import simplifier
import bz2
from os.path import exists

graphsUrl = 'https://www.openstreetmap.org/traces/page/'

def grab(fromPage, toPage, targetDirectory):

    if (not targetDirectory.endswith('/')) and (len(targetDirectory) > 0): targetDirectory += '/'
    
    for i in range(fromPage, toPage + 1):
        graphsRes = requests.get(graphsUrl + str(i))

        matches = set(re.findall("/traces/[0-9]*", graphsRes.text))

        for m in matches:
            split = m.split('/')
            trace = split[2]
            if len(trace) != 7: continue

            downloadUrl = 'https://www.openstreetmap.org/trace/' + str(trace) + '/data';

            downloadRes = requests.get(downloadUrl)

            file = re.findall("filename=(.+)", downloadRes.headers['content-disposition'])[0]
            if (not ".gpx" in file) and (not ".bz2" in file):
                continue

            content = downloadRes.content

            if ("bz2" in file):
                if not isinstance(content, bytes):
                    content = bytes(content, 'utf-8')
                content = bz2.decompress(content)
            
            data = str(content)

            simplified = simplifier.simplifyData(data)
            
            length = len(simplified)
            path = targetDirectory + str(length) + ".sgpx"
            if exists(path):
                continue

            out = open(path, "w")

            out.write(str(length) + "\n")

            for i in range(length):
                point = simplified[i]
                line = str(point[0]) + ',' + str(point[1])
                out.write(line)
                out.write('\n')

            out.close

    
grab(1, 1, "/Users/nick/Documents/University/Bachelor Project/gradual-line-simplification/python-workspace/data")

    
