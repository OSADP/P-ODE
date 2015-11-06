Prototype Operational Data Environment Overview
============================
Prototype Operational Data Environment (P-ODE)
1.0.0
The P-ODE is an internet application which aggregates transportation metrics from multiple sources
in real time and provides a common data format to access this data regardless of original data
source. The data types supported are speed, volume, occupancy, travel time, and weather, basic
safety message, and signal phase and timing.

Installation and usage instructions are specified in detail in the P-ODE user manual word document.

License information
-------------------
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
file except in compliance with the License.
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under
the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the specific language governing
permissions and limitations under the License.

System Requirements
-------------------------
The P-ODE 1.0.0 software has been designed to work on Linux servers with Intel Xeon (2.8 GHz) processors,
32 GB of RAM, at least 5GB of storage (~500 MB for the code and the remainder as database storage)
depending on how long database data is retained for. The modules of this application (collectors, core,
emulator) have been designed to run on 3 separate servers, though depending on available resources may
be run on the same computer.

Internet conenctivity is required to poll data sources.

The application requires the installation of a Java Runtime Environment 1.7 and creation of a MongoDB
instance.

Documentation
-------------
The P-ODE 1.0.0 software is packaged with a Word formatted user guide detailing installation and
usage of the P-ODE software components. 

Web sites
---------
The P-ODE 1.0.0 software is distributed through the USDOT's JPO Open Source Application Development Portal (OSADP)
http://itsforge.net/ 
