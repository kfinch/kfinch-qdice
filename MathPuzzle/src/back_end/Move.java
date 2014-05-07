package back_end;

public class Move {
	public final int firstIndex;
	public final int secondIndex;
	public final int opIndex;
	
	public Move(int firstIndex, int secondIndex, int opIndex){
		this.firstIndex = firstIndex;
		this.secondIndex = secondIndex;
		this.opIndex = opIndex;
	}
}
