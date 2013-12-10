To build project run:
mvn clean -Pupdate-widgetset vaadin:update-widgetset install

Web application can be extended if requested (and possible):
  * check of changing Y axis numbers to role labels (with deleting legend in role path tab)
  * creation of fixed size user form input with scrollbar instead of current 'pop-up' list
  * having multiple users visible at one chart as was before in role path tab and is not now
  (there is currently created new chart for new user), it would require creating some not precomputed values (e.g. -1 for undefined user role or most recent one)
  * notification/annotations for important events