============
Tyler Tolton
============
.. image:: https://img.shields.io/badge/language-Clojure-red.svg
:target: https://clojure.org/
.. image:: https://img.shields.io/badge/IDE-intellij-green.svg
:target: https://www.jetbrains.com/idea/
.. image:: https://img.shields.io/badge/overwatch-Tracer-blue.svg
:target: http://overwatch.gamepedia.com/Tracer
Record Linkage Challenge for Sortable
=====================================

Priorities - In Order
---------------------

- **Flexibility**: as this application was not designed in close communication with my end-users (sortable), it can effectively be
    considered a *minimum viable product*.  In order for it to be effective, it needs to be designed in such a way that
    allows for rapid iteration in response to changes in specification or new business needs, along with quickly
    reordering, adding to, and subtracting from the the overall program behavior.

- **Accuracy**: or, in other words, risk avoidance.  In practical software design, avoiding reputational and business risk
    must be at the forefront of the designers mind.  While gradual improvements in throughput can garner new business over time,
    losing business is much easier and much faster.  Therefore, this program takes multiple distinct approaches to
    identifying accurately and avoiding classification errors.  This includes creating mechanisms for manual adjustment
    of known red flags within the program

- **Speed**: This one is placed last intentionally.  The sorting behavior clocks at around 5 seconds all told. As no
    specification

Installation
------------

The standalone JAR is in the repo in ``/target``.  Clone the repo and run -

``java -jar path/to/standalone-file.jar <desired_result_file_name> <desired_secondary_file_name>``

``desired_result_file_name`` this defaults to results.txt.  Those listings that can be matched with exactly one product will be output here

``desired_secondary_file_name`` default secondary.txt.  Listings with no matches, and those rare few that necessarily match multiple listings will be output here.


https://docs.google.com/document/d/1ZYZ57I0giSbuRFePaoHV7cJbRSBmUcekkcuLYbI_mtY/edit?usp=sharing

