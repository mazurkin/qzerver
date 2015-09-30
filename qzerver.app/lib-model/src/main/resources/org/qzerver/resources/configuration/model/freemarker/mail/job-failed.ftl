[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]

[#-- @ftlvariable name="locale" type="java.util.Locale" --]
[#-- @ftlvariable name="timezone" type="java.util.TimeZone" --]

[#setting locale=locale.toString()]
[#setting time_zone=timezone.ID]
[#setting date_format="full"]
[#setting time_format="full"]
[#setting datetime_format="full"]
[#setting url_escaping_charset="UTF-8"]

Test