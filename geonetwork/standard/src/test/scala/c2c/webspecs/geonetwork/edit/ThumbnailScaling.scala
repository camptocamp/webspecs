package c2c.webspecs.geonetwork.edit

case class ThumbnailScaling (factor: Int = 180, scaleByWidth: Boolean = true) {
  def direction = if (scaleByWidth) "width" else "height"
}