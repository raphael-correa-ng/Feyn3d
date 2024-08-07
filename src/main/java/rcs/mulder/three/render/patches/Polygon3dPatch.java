package rcs.mulder.three.render.patches;

import rcs.mulder.three.geo.GeoUtils3d;
import rcs.mulder.three.gfx.Graphics3d;
import rcs.mulder.three.kernel.MulderRuntime;
import rcs.mulder.three.optics.LightingUtils;
import rcs.mulder.three.render.Pipeline3d;
import rcs.mulder.three.render.RenderOptions3d;
import rcs.mulder.three.render.RenderOptions3d.Option;
import rcs.mulder.three.render.renderers.Line3dRenderer;
import rcs.mulder.three.view.ViewUtils;
import rcs.mulder.three.render.renderers.Polygon3dRenderer;
import rcs.mulder.color.ColorUtils;
import rcs.mulder.color.MulderColor;
import rcs.mulder.math.Matrix44;
import rcs.mulder.math.Vector3d;

public class Polygon3dPatch extends Patch3d {
  
  protected Vector3d[] vertices;
  
  protected Vector3d center;

  public Polygon3dPatch(Vector3d[] vertices, MulderColor color, RenderOptions3d options) {
    super(color, options);
    this.vertices = vertices;
  } 

  @Override
  public final Vector3d getCenter() {
    return null != center 
        ? center
        : (center = GeoUtils3d.getCenter(vertices));
  }
  
  @Override
  public void render(Graphics3d graphics, Matrix44 view, Matrix44 projection, Matrix44 viewPort) {
    Vector3d center = GeoUtils3d.getCenter(vertices);
    Vector3d surfaceNormal = GeoUtils3d.getNormal(vertices);
    boolean isBackfaceToCamera = ViewUtils.isBackFace(MulderRuntime.getView().getCamera().getPosition(), center, surfaceNormal);
    
    if (isBackfaceToCamera && shouldCullIfBackface()) {
      return;
    }
    
    Vector3d[] viewVertices = Pipeline3d.toViewSpaceCoordinates(vertices, view);
    Vector3d[] clippedViewVertices = Pipeline3d.clipViewSpaceCoordinates(viewVertices);
    
    if (clippedViewVertices.length < 3) {
      return;
    }
    
    if (options.isEnabled(RenderOptions3d.Option.meshOnly)) {
      renderMesh(graphics, clippedViewVertices, projection, viewPort);
      return;
    }
    
    Vector3d[][] triangulatedClippedViewVertices = GeoUtils3d.triangulate(clippedViewVertices);
    
    boolean shouldReverseNormalForLighting = !options.isEnabled(Option.meshOnly) && !options.isEnabled(Option.bothSidesShaded) && isBackfaceToCamera;
    Vector3d normalForLighting = shouldReverseNormalForLighting ? surfaceNormal.mul(-1) : surfaceNormal;

    double intensity = 1.0;
    if (options.isEnabled(RenderOptions3d.Option.flatShaded)) {
      intensity = LightingUtils.computeLightingIntensity(center, normalForLighting);
    }

    int finalColor = ColorUtils.mulRGB(color.getRGBA(), intensity);
    if (options.isEnabled(RenderOptions3d.Option.applyLightingColor)) {
      finalColor = LightingUtils.applyLightsourceColorTo(center, normalForLighting, finalColor);
    } 
    
    for (Vector3d[] triangle : triangulatedClippedViewVertices) {
      Polygon3dRenderer.render(
          graphics,
          Pipeline3d.toDeviceCoordinates(triangle, projection, viewPort),
          finalColor);
    }
  }

  private void renderMesh(Graphics3d graphics, Vector3d[] clippedViewVertices, Matrix44 projection, Matrix44 viewPort) {
    Vector3d[] deviceCoordinates = Pipeline3d.toDeviceCoordinates(clippedViewVertices, projection, viewPort);
    
    for (int i = 0, j = 1; i < deviceCoordinates.length; i++, j++, j%=deviceCoordinates.length) {
      Line3dRenderer.render(
          graphics, 
          deviceCoordinates[i], 
          deviceCoordinates[j], 
          color.getRGBA());
    }
  }
  
  protected boolean shouldCullIfBackface() {
    return !options.isEnabled(Option.meshOnly) && options.isEnabled(Option.cullIfBackface) && !color.isTransparent();
  }
}
