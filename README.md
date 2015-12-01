# Data-Mining-Classification

The purpose of this project is to implement the Decision Tree Algorithm on two datasets.In each dataset, each row corresponds
to a record. The last column corresponds to the class label, and the remaining columns are the attributes. For each dataset,
we provide two versions: One is the original data. The other (processed version) is obtained by mapping attribute values to
integers so that it can be loaded to Matlab. README presents the meanings of the attributes in these two datasets. 

The code is written in Eclipse, so as such, if you use simply the two classes and csv files, and compile from the command 
line, it will not compile successfully. If you wish to run the two class files from command line, make sure to also download 
the unprocessed csv files you wish to use. The appropriate changes to make before compiling are as follows:

In HW4_Code.java:
  delete "package src;"
  delete "import src.Tree.Node;"
  In line 212, in the function inputs: "Node" should be "Tree.Node"

In Tree.java:
  delete "package src;"

With these changes, the files will compile correctly with the standard command:
  javac HW4_Code.java Tree.java

Unfortunately, I could not debug my GINI equation enough. This currently will run an infinite loop.
