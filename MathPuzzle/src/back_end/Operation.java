package back_end;

import java.lang.reflect.Method;

/**
 * Representation of one of an operator for use in this Math Puzzle.
 * 
 * @author Kelton Finch
 */
public class Operation {
	
	//The integer code representing what operation this is. Use one of the following constants for a pre-built operation,
	//or -1 for a custom operation. 
	public final int opCode;
	
	public final boolean isCommutative;
	public final String symbol;
	
	public static final int CUSTOM = -1;
	public static final int PLUS = 0;
	public static final int MINUS = 1;
	public static final int TIMES = 2;
	public static final int DIVIDE = 3;
	public static final int EXPONENT = 4;
	public static final int ROOT = 5;
	public static final int MODULO = 6;
	
	public static final int MAX_PIECE_SIZE = 999;
	public static final int MIN_PIECE_SIZE = -999;
	
	public Operation(int opCode){
		Character.toChars(0x00F7);
		
		this.opCode = opCode;
		switch(opCode){
		case PLUS:     isCommutative = true;  symbol = "+"; break;
		case MINUS:    isCommutative = false; symbol = "-"; break;
		case TIMES:    isCommutative = true;  symbol = "x"; break;
		case DIVIDE:   isCommutative = false; symbol = Character.toString(Character.toChars(0x00F7)[0]); break;
		case EXPONENT: isCommutative = false; symbol = "^"; break;
		case ROOT:     isCommutative = false; symbol = Character.toString(Character.toChars(0x221A)[0]); break;
		case MODULO:   isCommutative = false; symbol = "%"; break;
		//Don't hit this! Initialize custom ops with the other constructor TODO: should this throw an exception?
		default: 	   isCommutative = false; symbol = "?"; break;
		}
	}
	
	public Operation(Method m, boolean isCommutative, String symbol){
		opCode = CUSTOM;
		this.isCommutative = isCommutative;
		this.symbol = symbol;
		//TODO: NYI
	}
	
	public int operate(int first, int second) throws CombineException{
		int result;
		switch(opCode){
		case PLUS:
			result = first + second;
			break;
		case MINUS:
			result = first - second;
			break;
		case TIMES:
			result = first * second;
			break;
		case DIVIDE:
			if(second == 0)
				throw new CombineException("No dividing by zero");
			if(first % second != 0)
				throw new CombineException("No fractions");
			result = first / second;
			break;
		case EXPONENT:
			result = (int) Math.pow(first, second);
			break;
		case ROOT:
			//TODO: add fraction detection
			result = (int) Math.pow(first, 1/(double)(second));
			break;
		case MODULO:
			result = first % second;
			break;
		case CUSTOM:
			throw new CombineException("NYI"); //TODO: NYI
		default:
			throw new CombineException("Invalid op code");
		}
		
		if(result > MAX_PIECE_SIZE)
			throw new CombineException("Result too large");
		if(result < MIN_PIECE_SIZE)
			throw new CombineException("Result too small");
		
		return result;
	}
	
	public String toString(){
		return symbol;
	}
}
