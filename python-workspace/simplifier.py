import re


def simplifyData(data):
    points = re.findall("< *trkpt *lat=\"-?[0-9]*\.[0-9]*\" *lon=\"-?[0-9]*\.[0-9]*\" *>", data)

    simplified = []
    
    for point in points:
        split = point.split('\"')
        lat = split[1]
        lon = split[3]

        simplified.append((lat,lon))
    
    return simplified



def simplifyFile(filePath):
    if (not filePath.endswith('.gpx')):
        print('Not a gpx file!')
        return
    
    data = ''

    with open(filePath, 'r') as file:
        data = file.read()

    simplified = simplifyData(data)

    outputFile = filePath[:-3] + 'sgpx'

    out = open(outputFile, "w")

    length = len(simplified)

    out.write(str(length) + "\n")

    for i in range(length):
        point = simplified[i]
        out.write(str(point[0]) + ',' + str(point[1]))
        out.write('\n')
    
    out.close


# simplifyFile('/Users/nick/Documents/University/Bachelor Project/gradual-line-simplification/python-workspace/5326431.gpx')
