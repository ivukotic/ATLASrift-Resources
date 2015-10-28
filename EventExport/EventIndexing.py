#!/usr/bin/env python

import json
from datetime import datetime
from elasticsearch import Elasticsearch
from pprint import pprint

with open('event.json') as data_file:    
    data = json.load(data_file)

data['eventnr']=data['event number']
data['runnr']=data['run number']
del data['event number']
del data['run number']
ts=[]
for t in data['Tracks']:
    ts.append(data['Tracks'][t])
del data['Tracks']
data['tracks']=ts
data['timestamp'] = datetime.now()
data['description']='A high-mass dijet event. This event was collected in September 2015: the two central high-pT jets have an invariant mass of 8.8 TeV, the highest-pT jet has a pT of 810 GeV, and the subleading jet has a pT of 750 GeV. The missing ET for this event is 60 GeV.'
pprint(data)

ind="atlasrift_events"


print "make sure we are connected right..."
import requests
res = requests.get('http://cl-analytics.mwt2.org:9200')
print(res.content)


es = Elasticsearch([{'host':'cl-analytics.mwt2.org', 'port':9200}])


res = es.search(index=ind, body={"query": {"match_all": {}}})
print("Got %d Hits." % res['hits']['total'])

res = es.index(index=ind, doc_type='event', body=doc)
print(res['created'])