HDF5HadoopReader
================

HDF5 Reader for Hadoop MapReduce

History
-------

v0.1 - Initial upload to GitHub. 
     - Unstable.
     - Many datatypes and versions are not yet implemented.


Build
-----

Use "ANT" build system to compile source code.
Just type "ant".

"/libs" folder has some referenced libraries. These libraries were copied from Cloudera CDH3U5 distribution package. If you are using other version/distribution of Hadoop, you should change these libraries and build.xml.

References
----------

- Many HDF5 related source files are copied from NETCDF4 source code.
http://www.unidata.ucar.edu
- FrequencyCount example is originally written by MATTHEW ADAM JUSTICE
http://www.cs.arizona.edu/news/honors/JusticeMatt_HonorsThesis_SP12_Final.pdf
