create view view_user_leader as SELECT 
  "U1"."Username" as user, 
  "U2"."Username" as leader
FROM 
  public."User" "U1", 
  public."User" "U2"
WHERE 
  "U2"."Id" = "U1"."Code"::integer ;
