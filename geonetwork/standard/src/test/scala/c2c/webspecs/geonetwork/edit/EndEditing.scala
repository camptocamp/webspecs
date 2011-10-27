package c2c.webspecs
package geonetwork
package edit

object EndEditing extends Request[EditValue,IdValue] {
  def execute (in: EditValue)(implicit context:ExecutionContext) = UpdateMetadata().execute(in)
}