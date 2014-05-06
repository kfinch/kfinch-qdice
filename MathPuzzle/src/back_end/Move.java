package back_end;

public class Move {
	public final int first;
	public final int second;
	public final Operation op;
	
	public Move(int first, int second, Operation op){
		this.first = first;
		this.second = second;
		this.op = op;
	}
}
