Changes
===========

0.8.9
------
*  Moved Gson requests to another library at ```com.twistedequations.crossbow:crossbow-gson:{{version number}}```
*  Added ability to execute requests synchronously on the current thread
*  Updated compile sdk to 23
*  Updated support v4 to 23.1.0

0.8.7
------
*  Deprecated gson requests - these will be moved to their own library in 0.9
*  Removed access to the FileRequests and FileQueue. These are still used to back the crossbow image loader and you
can still use the file image loader.