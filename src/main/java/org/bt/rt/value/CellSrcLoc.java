package org.bt.rt.value;

public class CellSrcLoc extends Cell {
	private static final long serialVersionUID = -5188953181259118917L;
	
	public SrcLoc loc;
	
	public CellSrcLoc() {}	
	public CellSrcLoc(Object car, Object cdr, SrcLoc loc) {
		super(car, cdr);
		this.loc = loc;
	}

	public static CellSrcLoc make(Object car, Object cdr, SrcLoc loc) {
		return new CellSrcLoc(car, cdr, loc);
	}
	
	public static CellSrcLoc make(Object car, SrcLoc loc) {
		return new CellSrcLoc(car, null, loc);
	}
	
	public static CellSrcLoc make(SrcLoc loc) {
		return new CellSrcLoc(null, null, loc);
	}
	
}
