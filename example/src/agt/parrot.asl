topic("mqtt://test.mosquitto.org/hypermedea_test") .

+!start :
    topic(URI)
    <-
    watch(URI) ;
    !!publish .

+!publish :
    topic(URI)
    <-
    put(URI, text("WHAT?")) ;
    .wait(2000) ;
    !!publish .

+text(Msg)[source(URI)] :
    topic(URI)
    <-
    .print(Msg) .

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
