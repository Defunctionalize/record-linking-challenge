============
Tyler Tolton
============
.. image:: https://img.shields.io/badge/language-Clojure-red.svg
    :target: https://clojure.org/
.. image:: https://img.shields.io/badge/IDE-intellij-green.svg
    :target: https://www.jetbrains.com/idea/
.. image:: https://img.shields.io/badge/Overwatch-Tracer-blue.svg
    :target: https://www.reddit.com/r/Overwatch/


Record Linkage Challenge for Sortable
=====================================

Installation
------------

The standalone JAR is in the repo in ``/target``.  Clone the repo (or just download/extract the jar from the zip) and run -

``java -jar path/to/standalone-file.jar <desired_result_file_name> <desired_secondary_file_name>``

``desired_result_file_name``
    this defaults to results.txt.  Those listings that can be matched with exactly one product will be output here

``desired_secondary_file_name``
    default secondary.txt.  Listings with no matches, and those rare few that necessarily match multiple listings will be output here.



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

- **Speed**: The primary engine for speed here is parallelism with core.async.  We're leveraging immutable state to gain an
  infinitely parallelizable sequence of operations.  This makes scaling a pretty easy matter in the short run by throwing
  it onto a cloud server with multiple cpus.  Depending on your hardware configuration, you should see times between
  4 and 10 seconds for the sorting to complete.

Ultimately, in consideration of these priorities, I settled on a simple deterministic "pipeline of filters" approach to
processing the links. Each listing is initially examined as a potential match for every product, increasingly fine grained
filters are applied to the list of products until we are left with a final result that meets our human expectations of the
data output. Although a bit pedestrian compared to the probabilistic, feature scoring approach, deterministic record linkage
is much safer, much more accurate, and much easier to extend with new features.


Hire Me
=======

Thanks for taking a look at the code.

My name is Tyler Tolton. I've been a professional python and clojure developer for over 6 years, and in that time
I've worked with both massive companies and tiny startups, and I'm equally comfortable in both settings.  I
gravitate to companies that look out for their developers, by trusting them with ownership and protecting them
with strong adherence to good software engineering practices.  Communication, expectation, results.

If you're interested in taking a look at my professional history, `here's a google docs link`_ to my resume
.. _here's a google docs link: https://docs.google.com/document/d/1ZYZ57I0giSbuRFePaoHV7cJbRSBmUcekkcuLYbI_mtY/edit?usp=sharing

