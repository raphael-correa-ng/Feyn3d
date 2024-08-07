package demo3d;

import java.io.Serial;

import rcs.mulder.event.DragRotateObject;
import rcs.mulder.event.WheelZoomCamera;
import rcs.mulder.math.Vector3d;
import rcs.mulder.three.gui.AnimationCanvas3d;
import rcs.mulder.three.kernel.MulderRuntime;
import rcs.mulder.three.view.Camera3d;

public class Demo3d extends AnimationCanvas3d { 

  @Serial
  private static final long serialVersionUID = 1L;
  
  protected Camera3d camera = MulderRuntime.getView().getCamera();
  protected DragRotateObject drc = new DragRotateObject(camera, Vector3d.ZERO, 0.005);
  protected WheelZoomCamera wzc = new WheelZoomCamera(camera, 10);

  public Demo3d() { }

  @Override
  protected void initialize() {
    requestFocus(); 
    addKeyListener(this);
    wzc.setAmount(1);
    addDeltaMouseListener(drc);
    addMouseWheelListener(wzc);
  }

  protected final void controlCamera() {
    drc.update();
    wzc.update();
  }

  @Override
  public void runningLoop() { }

  @Override
  public void pausedLoop() { }
}
