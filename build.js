mkdir("bin");

javac("src", "bin");
copy("res", "bin");

mkdir("dist");
var name = "mappanel";
jar("dist/" + name + ".jar", "bin");

publish("dist")
