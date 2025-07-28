package zio.datastar.poc.datastar

enum ElementPatchMode(val value: String) {
  case Append  extends ElementPatchMode("append")
  case Prepend extends ElementPatchMode("prepend")
  case Replace extends ElementPatchMode("replace")
  case Update  extends ElementPatchMode("update")
  case Remove  extends ElementPatchMode("remove")
  case Upsert  extends ElementPatchMode("upsert")
  case Before  extends ElementPatchMode("before")
  case After   extends ElementPatchMode("after")
  case Outer   extends ElementPatchMode("outer")
  case Inner   extends ElementPatchMode("inner")
}
