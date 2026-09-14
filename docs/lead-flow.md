NEW
|
v
CONTACTED
|
+------------------------------+
|                              |
v                              v
WAITING_CLIENT           WAITING_SCHEDULE
|                              |
|                              |
+-------------+----------------+ 
              |
              v
          SCHEDULED
              |
         +----+----+
         |         |
         v         v
     COMPLETED  RESCHEDULE_REQUIRED
                   |
                   v
               SCHEDULED

NEW -> CONTACTED
NEW -> SCHEDULED

CONTACTED -> WAITING_CLIENT
CONTACTED -> WAITING_SCHEDULE
CONTACTED -> SCHEDULED
CONTACTED -> LOST

WAITING_CLIENT -> SCHEDULED
WAITING_CLIENT -> LOST

WAITING_SCHEDULE -> SCHEDULED
WAITING_SCHEDULE -> LOST

SCHEDULED -> COMPLETED
SCHEDULED -> RESCHEDULE_REQUIRED
SCHEDULED -> LOST

RESCHEDULE_REQUIRED -> SCHEDULED
RESCHEDULE_REQUIRED -> LOST