Open Source Overview
============================
Prototype Operational Data Environment (P-ODE)
Version 2.0.3

Description:
The P-ODE software is written in Java and is divided so that it can run on up to two Ubuntu servers if necessary, depending on system load. The collector server runs several collector processes, each of which connects to a specific data source (e.g. sensor hardware or external web service). The P-ODE core takes in data from all of the collectors and redistributes it to the various subscribers for real time dissemination. The core also pushes all of the data to the RDE system for permanent archival.  Finally, the core has the capability to service subscriptions for historical data replay by retrieving archived data from the RDE and serving it up to the subscriber.

CAUTION:  in mid-September, 2016, VDOT plans to change the format of their incident data. This means that the collector delivered in this version will no longer work correctly at that time. In particluar, VDOT stated the following on 7/21/16: 
"This past Tuesday, VDOT made the following changes upstream of these feeds, which have the noted impact to the feeds:

•	Addition of new “Disabled Tractor Trailer” incident type
o	This new type may now appear in the VaTraffic Incidents Feed in the <orci:type_event> field
•	Removal of the “Priority” field from Incidents, Planned Events and Weather Events
o	For now, TVD continues to provide this field in a deprecated manner, but the priority value for any event created since Tuesday will always be “Minor” or “Unknown” regardless of the event

On or about September 13, 2016, TVD will remove the Priority field from these feeds entirely.  Until then, the Priority field will continue to be populated as noted above, and should not be considered indicative of the actual severity of an event.

Users of the priority field should heed the following recommendations:

•	Utilize the severity field rather than the priority field.  Severities of Level I, Level II, and Level III correlate to priorities of minor, major, and high-profile respectively.  It is this direct association, and potential for mis-match, that led to the change to eliminate the priority field upstream from TVD.
•	Prepare for the absence of the priority field altogether prior to September 13."


Installation and removal instructions
-------------------------------------
Software installation instructions are detailed in the User Guide, which accompanies the source code in the OSADP repository.


License information
-------------------
See the accompanying LICENSE file.


System Requirements
-------------------------
The software can run on any modern generic laptop grade computer, but it may need more resources as the number of
data sources and the number of subscribers grows. 
Minimum memory:  2 GB
Processing power:  Intel Core I3 @ 1.6 GHz or equivalent
Connectivity:  ethernet
Operating systems supported:  Ubuntu 14.04


Documentation
-------------
A User Guide is included in the OSADP directory tree.


Web sites
---------
The software is distributed through the USDOT's JPO Open Source Application Development Portal (OSADP)
http://itsforge.net/ 
