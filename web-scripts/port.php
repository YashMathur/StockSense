<?php
include('simple_html_dom.php');

$all = array();

function get($f, $type){
	
	$arr = array($f."_".$type => array());
	if($type=="NSE"){
	
		$w = "http://www.google.com/finance?q=NSE%3A".urlencode($f)."&ei=W03_U-iBAoWekgWa4ICYBg";
		$dom = file_get_html($w);
	
	
		$n = $dom->find('div.appbar-snippet-primary span', 0)->innerhtml;
	
		$pp = $dom->find('div[id=price-panel]', 0)->childNodes(0);
		$val = $pp->childNodes(0)->childNodes(0)->innertext;
		$in_val = $pp->childNodes(1)->childNodes(0)->childNodes(0)->innertext;
		$in_perc = $pp->childNodes(1)->childNodes(0)->childNodes(1)->innertext;
		if($in_val==""){	
			$in_val = "N/A";
		}
	
		$pp = $dom->find('table.snap-data tbody', 0);
		$range = $pp->childnodes(0)->childnodes(1)->innertext;;
		$week_r = $pp->childnodes(1)->childnodes(1)->innertext;
		$open = $pp->childnodes(2)->childnodes(1)->innertext;
		$vol = $pp->childnodes(3)->childnodes(1)->innertext;
	
		if(is_object($pp->childnodes(4))){
		
			$cap = $pp->childnodes(4)->childnodes(1)->innertext;
			$price_earn = $pp->childnodes(5)->childnodes(1)->innertext;
	
			$pp1 = $dom->find('table.snap-data tbody', 1);
			$div = $pp1->childnodes(0)->childnodes(1)->innertext;
			$eps = $pp1->childnodes(1)->childnodes(1)->innertext;
			$shares = $pp1->childnodes(2)->childnodes(1)->innertext;
			$beta = $pp1->childnodes(3)->childnodes(1)->innertext;
			$inst = $pp1->childnodes(4)->childnodes(1)->innertext;
	
			$arr[$f."_".$type] = array("1"=> $in_perc, "2"=>$val, "3"=>$in_val, "4"=>$open);
			//echo json_encode($arr).",";
	
		}
	
		else{
			$arr[$f."_".$type]= array("1"=> $in_perc, "2"=>$val, "3"=>$in_val, "4"=>$open);
			//echo json_encode($arr).",";
		}
		
	}
	else if($type=="BSE"){
	
		$w = "http://www.google.com/finance?q=BOM%3A".urlencode($f)."&ei=fi1_VKnjIMePlAWTx4HgCA";
		$dom = file_get_html($w);
	
	
		$n = $dom->find('div.appbar-snippet-primary span', 0)->innerhtml;
	
		$pp = $dom->find('div[id=price-panel]', 0)->childNodes(0);
		$val = $pp->childNodes(0)->childNodes(0)->innertext;
		$in_val = $pp->childNodes(1)->childNodes(0)->childNodes(0)->innertext;
		$in_perc = $pp->childNodes(1)->childNodes(0)->childNodes(1)->innertext;
		if($in_val==""){	
			$in_val = "N/A";
		}
	
		$pp = $dom->find('table.snap-data tbody', 0);
		$range = $pp->childnodes(0)->childnodes(1)->innertext;;
		$week_r = $pp->childnodes(1)->childnodes(1)->innertext;
		$open = $pp->childnodes(2)->childnodes(1)->innertext;
		$vol = $pp->childnodes(3)->childnodes(1)->innertext;
	
		if(is_object($pp->childnodes(4))){
			$cap = $pp->childnodes(4)->childnodes(1)->innertext;
			$price_earn = $pp->childnodes(5)->childnodes(1)->innertext;
		
			$pp1 = $dom->find('table.snap-data tbody', 1);
			$div = $pp1->childnodes(0)->childnodes(1)->innertext;
			$eps = $pp1->childnodes(1)->childnodes(1)->innertext;
			$shares = $pp1->childnodes(2)->childnodes(1)->innertext;
			$beta = $pp1->childnodes(3)->childnodes(1)->innertext;
			$inst = $pp1->childnodes(4)->childnodes(1)->innertext;
		
			$arr[$f."_".$type]= array("1"=> $in_perc, "2"=>$val, "3"=>$in_val, "4"=>$open);
			//echo json_encode($arr).",";
	
		}
	
		else{
		$arr[$f."_".$type]=array("1"=> $in_perc, "2"=>$val, "3"=>$in_val, "4"=>$open);
		//echo json_encode($arr).",";
		}
	
	}
	else if($type=="INDEXBOM"){
	
		$w = "http://www.google.com/finance?q=INDEXBOM%3A".urlencode($f)."&ei=fi1_VKnjIMePlAWTx4HgCA";
		$dom = file_get_html($w);
	
	
		$n = $dom->find('div.appbar-snippet-primary span', 0)->innerhtml;
	
		$pp = $dom->find('div[id=price-panel]', 0)->childNodes(0);
		$val = $pp->childNodes(0)->childNodes(0)->innertext;
		$in_val = $pp->childNodes(1)->childNodes(0)->childNodes(0)->innertext;
		$in_perc = $pp->childNodes(1)->childNodes(0)->childNodes(1)->innertext;
		if($in_val==""){	
			$in_val = "N/A";
		}
	
		$pp = $dom->find('table.snap-data tbody', 0);
		$range = $pp->childnodes(0)->childnodes(1)->innertext;;
		$week_r = $pp->childnodes(1)->childnodes(1)->innertext;
		$open = $pp->childnodes(2)->childnodes(1)->innertext;
		$vol = $pp->childnodes(3)->childnodes(1)->innertext;
	
		if(is_object($pp->childnodes(4))){
			$cap = $pp->childnodes(4)->childnodes(1)->innertext;
			$price_earn = $pp->childnodes(5)->childnodes(1)->innertext;
		
			$pp1 = $dom->find('table.snap-data tbody', 1);
			$div = $pp1->childnodes(0)->childnodes(1)->innertext;
			$eps = $pp1->childnodes(1)->childnodes(1)->innertext;
			$shares = $pp1->childnodes(2)->childnodes(1)->innertext;
			$beta = $pp1->childnodes(3)->childnodes(1)->innertext;
			$inst = $pp1->childnodes(4)->childnodes(1)->innertext;
		
			$arr[$f."_".$type]= array("1"=> $in_perc, "2"=>$val, "3"=>$in_val, "4"=>$open);
			//echo json_encode($arr).",";
	
		}
	
		else{
		$arr[$f."_".$type]= array("1"=> $in_perc, "2"=>$val, "3"=>$in_val, "4"=>$open);
		//echo json_encode($arr).",";
		}
	
	}
	
	//array_merge($arr,$all);
	//echo "<br>ALL=".json_encode($all)."<br>";
	return $arr;

}

$j = $_GET['j'];
$json = json_decode($j);
$res = array();
foreach($json->portfolio as $s){
	$res = array_merge($res,get($s->code, $s->type));
}
echo json_encode($res);

?>