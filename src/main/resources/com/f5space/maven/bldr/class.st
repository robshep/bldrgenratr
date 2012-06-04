package $bldr.pkg$;

/**
 * Created by bldr
 */
public class $bldr.className$
{
	protected $bldr.type$ ting;
	
	$bldr.bldrs:{b|public $bldr.className$ with$b.propNameU$($b.propArgsSpec$) { 
	ting.$b.setMethodName$($b.propArgsList$); 
	return this; 
}
	};separator="\n"$
	
	public static $bldr.className$ nu() {
		return new $bldr.className$();
	}
	
	private $bldr.className$() {
		ting = new $bldr.type$();
	}
	
	public $bldr.type$ get() {
		return ting;
	}
}