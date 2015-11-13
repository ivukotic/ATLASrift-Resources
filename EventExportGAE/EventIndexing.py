#!/usr/bin/env python

import requests,json
from datetime import datetime
from pprint import pprint

with open('EventDump.json') as data_file:    
    data = json.load(data_file)

data['eventnr']=data['event number']
data['runnr']=data['run number']
del data['event number']
del data['run number']

elem='xAOD::Type::TrackParticle'
for t in data[elem]:
    print t
    ts=[]
    for coll in data[elem][t]:
        print coll
        ts.append(data[elem][t][coll])
    data[elem][t]=ts

elem='xAOD::Type::Jet'
for t in data[elem]:
    print t
    ts=[]
    for coll in data[elem][t]:
        print coll
        ts.append(data[elem][t][coll])
    data[elem][t]=ts
    
elem='xAOD::Type::CaloCluster'    
for t in data[elem]:
    print t
    ts=[]
    for coll in data[elem][t]:
        print coll
        ts.append(data[elem][t][coll])
    data[elem][t]=ts

data['eventid'] = 1
data['description']='A high-mass dijet event. This event was collected in September 2015: the two central high-pT jets have an invariant mass of 8.8 TeV, the highest-pT jet has a pT of 810 GeV, and the subleading jet has a pT of 750 GeV. The missing ET for this event is 60 GeV.'
#pprint(data)


GAEurl= 'http://atlasrift.appspot.com/eventserver'
# data = json.dumps({'name':'test', 'description':'some test repo'})
headers = {'content-type': 'application/json'}
r = requests.post(GAEurl, data=json.dumps(data), headers=headers)
print r.text