https://mermaid-js.github.io/mermaid-live-editor/

https://plantuml.com/sequence-diagram
https://plantuml-documentation.readthedocs.io/en/latest/formatting/all-skin-params.html
https://plantuml.com/skinparam
https://github.com/VladimirAlexiev/plantuml-skinparam
https://real-world-plantuml.com/?type=sequence
https://plantuml-editor.kkeisuke.com/

https://sequencediagram.org/
https://www.websequencediagrams.com/
https://www.websequencediagrams.com/old/
https://swimlanes.io/

https://www.umlet.com/
https://app.zenuml.com/
https://app.genmymodel.com/
https://app.creately.com/diagram
https://online.visual-paradigm.com/app/diagrams

int c;
String responseLine;

while ((c = System.in.read()) != -1) {
    os.write((byte)c);
    if (c == '\n') {
        os.flush();
        responseLine = is.readLine();
        System.out.println("echo: " + responseLine);
    }
}

String userInput;
while ((userInput = stdIn.readLine()) != null) {
    out.println(userInput);
    System.out.println("echo: " + in.readLine());
}

        if (new String(buffer.array()).trim().equals(POISON_PILL)) {
            client.close();
            System.out.println("Not accepting client messages anymore");
        }