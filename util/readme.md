# Gradual Line Simplification

## Util

### Simplifier

---

The [simplifier.py](simplifier.py) file is a python program that lets you simplify gpx traces to a simpler format called sgpx. It only contains relevant info and removes time stamps.

Usage:

    import simplifier
    simplifier.simplifyFile('pathToFile')

This will create a sgpx file with the same name. It will only accept files with .gpx file extensions.

### Grabber

---

The [grabber.py](grabber.py) file is a python program that grabs real world gps-traces from [https://www.openstreetmap.org/traces](https://www.openstreetmap.org/traces) and transforms them into the sgpx format.

Usage:

    import grabber
    grabber.grab(fromPage, toPage, pathToFolder)

where `fromPage` is the page the grabber starts and `toPage` is the page the grabber stops (included). Each page consists of 20 gps-traces.
