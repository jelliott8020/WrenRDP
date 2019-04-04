import com.sun.org.apache.bcel.internal.generic.MULTIANEWARRAY;

public class WrenRDP extends RDP implements WrenTokens {
    public static void main(String args[]) {
        String input = "";
        if (args.length == 0) {
            System.out.println("Need program-string arg.");
            System.out.println("Example use: java WrenRDP \"`cat test.w`\"");
            System.exit(1);
        }
        for (String s : args)
            input = input + s + "\n";

        System.out.println("Input:\n" + input + "\nEndInput\n");
        WrenRDP p = new WrenRDP(input);
        p.parse();
    }

    public WrenRDP(String input) {
        lexer = new WrenLexer(input);
    }

    public void StartSymbol() {
        program();
    }

    private void program() {
        if (currTok == PROG_TOK) {
            match(PROG_TOK);
            match(VARIABLE_TOK);
            match(IS_TOK);
            block();
        } else {
            error();
        }
    }

    private void block() {
        if (currTok == VAR_TOK) {
            decseq();
            match(BEGIN_TOK);
            commandseq();
            match(END_TOK);
        } else {
            error();
        }
    }

    private void decseq() {
        if (currTok == VAR_TOK) {
            dec();
            decseq();
        } else {
            lambda();
        }
    }

    private void dec() {
        if (currTok == VAR_TOK) {
            match(VAR_TOK);
            varlist();
            match(COLON_TOK);
            type();
            match(SEMICOLON_TOK);
        } else {
            error();
        }
    }

    private void type() {
        if (currTok == INT_TOK) {
            match(INT_TOK);
        } else if (currTok == BOOL_TOK) {
            match(BOOL_TOK);
        } else {
            error();
        }
    }

    private void varlist() {
        if (currTok == VARIABLE_TOK) {
            match(VARIABLE_TOK);
            varlist2();
        } else {
            error();
        }
    }

    private void varlist2() {
        if (currTok == SEMICOLON_TOK) {
            match(SEMICOLON_TOK);
            varlist();
        } else {
            lambda();
        }
    }

    private void commandseq() {
        if (currTok == SKIP_TOK || currTok == READ_TOK || currTok == WRITE_TOK || currTok == WHILE_TOK
                || currTok == IF_TOK) {
            command();
            commandseq2();
        } else {
            error();
        }
    }

    private void commandseq2() {
        if (currTok == SEMICOLON_TOK) {
            match(SEMICOLON_TOK);
            commandseq();
        } else {
            lambda();
        }
    }

    private void command() {
        if (currTok == VARIABLE_TOK) {
            assign();
        } else if (currTok == SKIP_TOK) {
            match(SKIP_TOK);
        } else if (currTok == READ_TOK) {
            match(READ_TOK);
            match(VARIABLE_TOK);
        } else if (currTok == WRITE_TOK) {
            match(WRITE_TOK);
            intexpr();
        } else if (currTok == WHILE_TOK) {
            match(WHILE_TOK);
            boolexpr();
            match(DO_TOK);
            commandseq();
            match(END_TOK);
            match(WHILE_TOK);
        } else if (currTok == IF_TOK) {
            match(IF_TOK);
            boolexpr();
            match(THEN_TOK);
            commandseq();
            command2();
        } else {
            error();
        }
    }

    private void command2() {
        if (currTok == END_TOK) {
            match(END_TOK);
            match(IF_TOK);
        } else if (currTok == ELSE_TOK) {
            match(ELSE_TOK);
            commandseq();
            match(END_TOK);
            match(IF_TOK);
        } else {
            error();
        }
    }

    private void assign() {
        if (currTok == VARIABLE_TOK) {
            match(VARIABLE_TOK);
            assign2();
        } else {
            error();
        }
    }

    private void assign2() {
        if (currTok == INTASSIGN_TOK) {
            match(INTASSIGN_TOK);
            intexpr();
        } else if (currTok == BOOLASSIGN_TOK) {
            match(BOOLASSIGN_TOK);
            boolexpr();
        } else {
            error();
        }
    }

    private void intexpr() {
        if (currTok == INTCONST_TOK) {
            intterm();
            intexpr2();
        } else {
            error();
        }
    }

    private void intexpr2() {
        if (currTok == MINUS_TOK || currTok == PLUS_TOK) {
            weak_op();
            intterm();
            intexpr2();
        } else {
            lambda();
        }
    }

    private void intterm() {
        if (currTok == INTCONST_TOK) {
            intelement();
            intterm2();
        } else {
            error();
        }
    }

    private void intterm2() {
        if (currTok == MULT_TOK || currTok == DIV_TOK) {
            strong_op();
            intelement();
            intterm2();
        } else {
            lambda();
        }
    }

    private void intelement() {
        if (currTok == INTCONST_TOK) {
            match(INTCONT_TOK);
        } else if (currTok == VARIABLE_TOK) {
            match(VARIABLE_TOK);
        } else if (currTok == RPAR_TOK) {
            match(RPAR_TOK);
            intexpr();
            match(LPAR_TOK);
        } else if (currTok == MINUS_TOK) {
            match(MINUS_TOK);
            intelement();
        } else {
            error();
        }
    }

    private void boolexpr() {
        if (currTok == TRUE_TOK || currTok == FALSE_TOK || currTok == NOT_TOK || currTok == RBRACK_TOK
                || currTok == VARIABLE_TOK || currTok == INTCONST_TOK || currTok == RPAR_TOK || currTok == MINUS_TOK) {

            boolterm();
            boolexpr2();
        } else {
            error();
        }
    }

    private void boolexpr2() {
        if (currTok == OR_TOK) {
            boolterm();
            boolexpr2();
        } else {
            lambda();
        }
    }

    private void boolterm() {
        if (currTok == TRUE_TOK || currTok == NOT_TOK || currTok == RBRACK_TOK || currTok == VARIABLE_TOK
                || currTok == INTCONST_TOK || currTok == RPAR_TOK || currTok == MINUS_TOK) {

            boolelement();
            boolterm2();
        } else {
            error();
        }
    }

    private void boolterm2() {
        if (currTok == AND_TOK) {
            boolelement();
            boolterm2();
        } else {
            lambda();
        }
    }

    private void relation() {
        if (currTok == LE_TOK) {
            match(LE_TOK);
        } else if (currTok == LT_TOK) {
            match(LT_TOK);
        } else if (currTok == EQ_TOK) {
            match(EQ_TOK);
        }

        switch (currTok) {
        case LE_TOK:
            match(LE_TOK);
            break;
        case LT_TOK:
            match(LT_TOK);
            break;
        case EQ_TOK:
            match(EQ_TOK);
            break;
        case NE_TOK:
            match(NE_TOK);
            break;
        case GE_TOK:
            match(GE_TOK);
            break;
        case GT_TOK:
            match(GT_TOK);
            break;
        default:
            error();
        }
    }

    private void strong_op() {
        if (currTok == MUL_TOK) {
            match(MUL_TOK);
        } else if (currTok == DIV_TOK) {
            match(DIV_TOK);
        } else {
            error();
        }
    }

    private void weak_op() {
        if (currTok == PLUS_TOK) {
            match(PLUS_TOK);
        } else if (currTok == MINUS_TOK) {
            match(MINUS_TOK);
        } else {
            error();
        }
    }

    private void lambda() {

    }

    private void boolelement() {
        if (currTok == TRUE_TOK)
            match(TRUE_TOK);
        else if (currTok == FALSE_TOK)
            match(FALSE_TOK);
        else if (currTok == NOT_TOK) {
            match(NOT_TOK);
            match(LBRACK_TOK);
            boolexpr();
            match(RBRACK_TOK);
        } else if (currTok == LBRACK_TOK) {
            match(LBRACK_TOK);
            boolexpr();
            match(RBRACK_TOK);
        } else if (currTok == VARIABLE_TOK) {
            // jbf : VARIABLE ALONE OR COMPARISON, PREDICTION PROBLEM
            currTok = lexer.getToken();
            switch (currTok) {
            case LT_TOK:
            case LE_TOK:
            case GT_TOK:
            case GE_TOK:
            case EQ_TOK:
            case NE_TOK:
                relation();
                intexpr();
                break;
            case MUL_TOK:
            case DIV_TOK:
                intterm2();
                relation();
                intexpr();
                break;
            case PLUS_TOK:
            case MINUS_TOK:
                intexpr2();
                relation();
                intexpr();
                break;
            }
        } else if (currTok == INTCONST_TOK || currTok == LPAR_TOK || currTok == MINUS_TOK) {
            intexpr();
            relation();
            intexpr();
        } else
            error("boolelement");
    }
}