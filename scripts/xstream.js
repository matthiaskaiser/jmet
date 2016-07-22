function payload(target){

        var imports = new JavaImporter(java.io, java.lang, com.thoughtworks.xstream);
        with (imports) {

            xstream = new XStream();
            target.addTextPayload("test",xstream.toXML("Object"));

        }
}
