#!/usr/bin/env python

import json
from datetime import datetime
from elasticsearch import Elasticsearch
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

data['timestamp'] = datetime.now()
data['description']='A high-mass dijet event. This event was collected in September 2015: the two central high-pT jets have an invariant mass of 8.8 TeV, the highest-pT jet has a pT of 810 GeV, and the subleading jet has a pT of 750 GeV. The missing ET for this event is 60 GeV.'
#pprint(data)

ind="atlasrift_events"


print "make sure we are connected right..."
import requests
res = requests.get('http://cl-analytics.mwt2.org:9200')
print(res.content)


es = Elasticsearch([{'host':'cl-analytics.mwt2.org', 'port':9200}])


# res = es.search(index=ind, body={"query": {"match_all": {}}})
# print("Got %d Hits." % res['hits']['total'])

res = es.index(index=ind, id=0, doc_type='event', body=data)
print(res['created'])
