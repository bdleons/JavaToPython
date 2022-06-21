import java.util.ArrayList;
import java.util.Collections;

public class Java2Python extends JavaParserBaseListener {

    public String scannerIdentifier = "";
    public String mainClass = "";
    public boolean inBodyClass = false;
    public boolean comaOnParamenters = false;
    public boolean comaOnArguments = false;
    public boolean exitsMainMethod = false;
    public boolean plusOperator = false;

    public int numTabs = 0;
    public int numOfAdditiveExpression = 0;
    public ArrayList<String> mainArray = new ArrayList<String>();
    public void addToArray(String cadena){
            mainArray.add(cadena);
    }

    @Override
    public void exitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        if(exitsMainMethod){
            String temp = "\n\nif __name__ == '__main__':\n\n";
            temp = temp + "\t\t" + mainClass + ".main([])";
            addToArray(temp);
        }
        for (int i = 0; i < mainArray.size(); i++) {
            System.out.print(mainArray.get(i));
        }
    }

    @Override
    public void enterCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        addToArray("import math\n");
    }

    @Override
    public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx){
        if(ctx.PACKAGE() != null){
            String temp = "#" + ctx.PACKAGE().getText() +" "+ ctx.packageName().Identifier().getText() + ctx.SEMI().getText() + "\n";
            addToArray(temp);
        }
    }

    @Override
    public void enterNormalClassDeclaration(JavaParser.NormalClassDeclarationContext ctx){
        if(inBodyClass){
            numTabs++;
        }
        if(ctx.CLASS() != null){
            if(mainClass.length() == 0){
                mainClass = ctx.Identifier().getText();
            }
            String temp = String.join("", Collections.nCopies(numTabs, "\t"));
            temp = temp + "class " +ctx.Identifier().getText() + ":\n";
            addToArray(temp);
        }

        numTabs++;
    }

    @Override
    public void enterClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx){
        inBodyClass = true;
    }

    @Override
    public void exitClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx){
        inBodyClass = false;
    }

    @Override
    public void enterMethodDeclarator(JavaParser.MethodDeclaratorContext ctx){
        String temp = String.join("", Collections.nCopies(numTabs, "\t"));
        if(ctx.Identifier().getText().equals("main")){
            exitsMainMethod = true;
        }
        temp = temp + "def " + ctx.Identifier().getText() + "(";
        addToArray(temp);
    }

    @Override
    public void exitFormalParameterList(JavaParser.FormalParameterListContext ctx){
        String temp = "):\n";
        addToArray(temp);
    }

    @Override
    public void enterFormalParameters(JavaParser.FormalParametersContext ctx){
        if(ctx.COMMA() != null){
            comaOnParamenters = true;
        }
    }

    @Override
    public void exitFormalParameters(JavaParser.FormalParametersContext ctx){
        comaOnParamenters = false;
    }

    @Override
    public void enterFormalParameter(JavaParser.FormalParameterContext ctx){
        String temp = ctx.variableDeclaratorId().getText();
        if(comaOnParamenters){
            temp = temp + ",";
        }
        addToArray(temp);
    }

    @Override
    public void enterVariableDeclarator(JavaParser.VariableDeclaratorContext ctx){
       if(ctx.variableDeclaratorId() != null){
           if(ctx.variableDeclaratorId().getParent().getText().contains("Scanner")){
               scannerIdentifier = ctx.variableDeclaratorId().getText();
           }else{
               String temp = String.join("", Collections.nCopies(numTabs, "\t"));
               temp = temp + ctx.variableDeclaratorId().getText();
               if(ctx.ASSIGN() != null){
                   temp = temp + " = ";
               }
               addToArray(temp);
           }
       }
    }

    @Override
    public void exitVariableDeclarator(JavaParser.VariableDeclaratorContext ctx){
        String temp = "\n";
        addToArray(temp);
    }

    @Override
    public void enterMethodBody(JavaParser.MethodBodyContext ctx){
        numTabs++;
    }

    @Override
    public void exitMethodBody(JavaParser.MethodBodyContext ctx){
        numTabs--;
    }

    @Override
    public void enterAdditiveExpression(JavaParser.AdditiveExpressionContext ctx){
        if(ctx.ADD() != null){
            plusOperator = true;
            numOfAdditiveExpression++;
        }
    }

    @Override
    public void exitAdditiveExpression(JavaParser.AdditiveExpressionContext ctx){
        numOfAdditiveExpression--;
        if(numOfAdditiveExpression==0){
            plusOperator = false;
        }
    }

    @Override
    public void enterExpressionName(JavaParser.ExpressionNameContext ctx){
        if(ctx.Identifier() != null){
            if(!ctx.Identifier().getText().equals("in")){
                String temp = ctx.Identifier().getText();
                addToArray(temp);

                if(comaOnArguments){
                    String aux = ", ";
                    addToArray(aux);
                }
            }
        }
        if(plusOperator){
            String temp = " + ";
            addToArray(temp);
        }
    }

    @Override
    public void enterReturnStatement(JavaParser.ReturnStatementContext ctx){
        String temp = String.join("", Collections.nCopies(numTabs, "\t"));
        if(ctx.RETURN() != null){
            temp = temp + "return ";
        }
        addToArray(temp);
    }

    @Override
    public void exitReturnStatement(JavaParser.ReturnStatementContext ctx){
        String temp = "\n";
        addToArray(temp);
    }

    @Override
    public void enterMethodInvocation_lfno_primary(JavaParser.MethodInvocation_lfno_primaryContext ctx){
        if(ctx.Identifier() != null){
            if(ctx.Identifier().getText().equals("parseInt")){
                String temp = "int(";
                addToArray(temp);
            }
        }
    }

    @Override
    public void enterTypeName(JavaParser.TypeNameContext ctx){
        if(ctx.Identifier().getText().equals(scannerIdentifier)){
            String temp = "input())";
            addToArray(temp);
        }
    }

    @Override
    public void enterMethodName(JavaParser.MethodNameContext ctx){
        if(ctx.Identifier() != null){
            String temp = mainClass + "." + ctx.Identifier().getText() + "(";
            addToArray(temp);
        }
    }

    @Override
    public void enterArgumentList(JavaParser.ArgumentListContext ctx){
        //String temp = ctx.getParent().getText();
        //addToArray("\n Parent on Argument List: " + temp + "\n");
        if(ctx.getParent().getText().contains("println")){
            String temp = String.join("", Collections.nCopies(numTabs, "\t"));
            addToArray(temp + "print(");
        }
        if(ctx.COMMA() != null){
            comaOnArguments = true;
        }
    }

    @Override
    public void exitArgumentList(JavaParser.ArgumentListContext ctx){
        comaOnArguments =false;
        if(!comaOnArguments){
            if(mainArray.get(mainArray.size()-1).contains(",")){
                mainArray.remove(mainArray.size()-1);
                addToArray(")");
            }
        }
    }

    @Override
    public void exitMethodInvocation(JavaParser.MethodInvocationContext ctx){
        String temp = ")";
        addToArray(temp);
    }
}
