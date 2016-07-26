Open Source Overview
============================
Prototype Operational Data Environment (P-ODE)
Version 2.0

Description:
The P-ODE software is written in Java and is divided so that it can run on up to two Ubuntu servers if necessary, depending on system load. The collector server runs several collector processes, each of which connects to a specific data source (e.g. sensor hardware or external web service). The P-ODE core takes in data from all of the collectors and redistributes it to the various subscribers for real time dissemination. The core also pushes all of the data to the RDE system for permanent archival.  Finally, the core has the capability to service subscriptions for historical data replay by retrieving archived data from the RDE and serving it up to the subscriber.


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
