import requests
import re
import simplifier

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

            out = open(targetDirectory + str(trace) + ".sgpx", "w")

            data = str(downloadRes.content)

            simplified = simplifier.simplifyData(data)
            
            length = len(simplified)

            out.write(str(length) + "\n")

            for i in range(length):
                point = simplified[i]
                line = str(point[0]) + ',' + str(point[1])
                out.write(line)
                out.write('\n')

            out.close

    
# grab(1, 1, "/Users/nick/Documents/University/Bachelor Project/gradual-line-simplification/python-workspace/data")

    
