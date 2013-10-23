package oripa.paint.mirror;

import java.awt.geom.Point2D;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.paint.CreasePatternInterface;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.PickingLine;
import oripa.paint.creasepattern.Painter;
import oripa.value.OriLine;

//TODO separate into FirstState and LatterState to delete state variable

public class SelectingLineForMirror extends PickingLine {

	
	
	public SelectingLineForMirror() {
		super();
	}

	@Override
	protected void initialize() {
	}

	
	private OriLine axis;
	private boolean doingFirstAction = true;
	
	/**
	 * This class keeps selecting line while {@code doSpecial} is false.
	 * When {@value doSpecial} is true, it executes mirror copy where the
	 * axis of mirror copy is the selected line.
	 * 
	 * @param doSpecial true if copy should be done.
	 * @return true if copy is done.
	 */
	@Override
	protected boolean onAct(PaintContextInterface context, Point2D.Double currentPoint,
			boolean doSpecial) {
		if(doingFirstAction){
			doingFirstAction = false;
			ORIPA.doc.cacheUndoInfo();
			
		}

		boolean result = super.onAct(context, currentPoint, doSpecial);
		
		if (result == true) {
			if (doSpecial) {
				axis = context.popLine();
				result = true;
            } 
			else {
				OriLine line = context.peekLine();

				if (line.selected) {
                	line.selected = false;
                	context.popLine();
                	context.removeLine(line);
                }
                else {
                	line.selected = true;
                }

                result = false;
            }
		}
		

		return result;
	}

	
	
	@Override
	protected void undoAction(PaintContextInterface context) {
//		if (doingFirstAction) {
//			super.undoAction(context);
//			return;
//		}
		context.popLine();
	}

	@Override
	protected void onResult(PaintContextInterface context) {

		Doc document = ORIPA.doc;
		CreasePatternInterface creasePattern = document.getCreasePattern();
		document.pushCachedUndoInfo();
		
        Painter painter = new Painter();
		painter.mirrorCopyBy(axis, context.getPickedLines(), creasePattern);

        doingFirstAction = true;
        context.clear(true);
	}

}
