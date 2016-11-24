package de.x28hd.tool;
import java.awt.Rectangle;
import java.util.HashSet;

import javax.swing.tree.DefaultTreeModel;

interface GraphPanelControler {
	void nodeSelected(GraphNode node);
	void edgeSelected(GraphEdge edge);
	void graphSelected();
//	void showNodeMenu(GraphNode node, int x, int y);
//	void showEdgeMenu(GraphEdge edge, int x, int y);
//	void showGraphMenu(int x, int y);
	void displayContextMenu(String menuID, int x, int y) ;
	void displayPopup(String msg);
	TextEditorPanel edi = null;
	void addToLabel(String text);
	void setFilename(String filename, int type);
	void triggerUpdate(boolean justOneMap);
	NewStuff getNSInstance();
	CompositionWindow getCWInstance();
	void finishCompositionMode();
	void manip(int x);
	void beginTranslation();
	void beginCreatingEdge();
	void beginLongTask();
	void endTask();
	GraphEdge createEdge(GraphNode topic1, GraphNode topic2);
	void setDefaultCursor();
	public void setWaitCursor();
	void setDirty(boolean toggle);
	void updateBounds();
	String getNewNodeColor();
	Rectangle getBounds();
	void setSystemUI(boolean toggle);
	void toggleAltColor(boolean down);
	void setTreeModel(DefaultTreeModel model);
	DefaultTreeModel getTreeModel();
	void setNonTreeEdges(HashSet<GraphEdge> nonTreeEdges);
	HashSet<GraphEdge> getNonTreeEdges();
	}
