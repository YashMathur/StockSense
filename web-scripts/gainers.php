<?php

include('simple_html_dom.php');
$f = "http://www.moneycontrol.com/stocks/marketstats/nsegainer/";
$s = "http://www.moneycontrol.com/stocks/marketstats/nseloser/";
$dom1 = file_get_html($f);
$dom2 = file_get_html($s);
$arr = array("data" => array());
$g = array("gain" => array());
$l = array("lose" => array());
$gb = array("gainb" => array());
$lb = array("loseb" => array());


$number = count($dom1->find('.tbldata14 tr'));
if($number>11){
	$number = 11;
}


for($i=1;$i<$number;$i++){
$n = $dom1->find('.tbldata14', 0)->childNodes($i)->childNodes(0)->find('a', 0)->find('b',0)->plaintext;
$ltp =$dom1->find('.tbldata14', 0)->childNodes($i)->childNodes(3)->plaintext;
$change = $dom1->find('.tbldata14', 0)->childNodes($i)->childNodes(6)->plaintext;
$knse[] = array('symbol' =>$n, 'ltp' => $ltp, 'netPrice' => $change);
}



$number = count($dom2->find('.tbldata14 tr'));
if($number>10){
	$number = 10;
}

for($i=1;$i<$number;$i++){
$n = $dom2->find('.tbldata14', 0)->childNodes($i)->childNodes(0)->find('a', 0)->find('b',0)->plaintext;
$ltp =$dom2->find('.tbldata14', 0)->childNodes($i)->childNodes(3)->plaintext;
$change = $dom2->find('.tbldata14', 0)->childNodes($i)->childNodes(6)->plaintext;
$knsel[] = array('symbol' =>$n, 'ltp' => $ltp, 'netPrice' => $change);
}

$g['gain'] = $knse;
$l['lose'] = $knsel;


$w = "http://www.moneycontrol.com/stocks/marketstats/bsegainer/";
$dom = file_get_html($w);
$number = count($dom->find('.tbldata14 tr'));
if($number>10){
	$number = 10;
}
	
for($i=1;$i<$number;$i++){
$n = $dom->find('.tbldata14', 0)->childNodes($i)->childNodes(0)->find('a', 0)->find('b',0)->plaintext;
$ltp =$dom->find('.tbldata14', 0)->childNodes($i)->childNodes(3)->plaintext;
$change = $dom->find('.tbldata14', 0)->childNodes($i)->childNodes(6)->plaintext;
$kb[] = array('symbol' =>$n, 'ltp' => $ltp, 'netPrice' => $change);
}
$gb['gainb'] = $kb;

$w = "http://www.moneycontrol.com/stocks/marketstats/bseloser/";
$dom = file_get_html($w);

$number = count($dom->find('.tbldata14 tr'));
if($number>10){
	$number = 10;
}


for($i=1;$i<$number;$i++){
$n = $dom->find('.tbldata14', 0)->childNodes($i)->childNodes(0)->find('a', 0)->find('b',0)->plaintext;
$ltp =$dom->find('.tbldata14', 0)->childNodes($i)->childNodes(3)->plaintext;
$change = $dom->find('.tbldata14', 0)->childNodes($i)->childNodes(6)->plaintext;
$mb[] = array('symbol' =>$n, 'ltp' => $ltp, 'netPrice' => $change);
}
$lb['loseb'] = $mb;

$cnx = "http://www.google.com/finance?q=NSE%3ANIFTY&ei=VoKiVIigLMSxkAWZsYGwCA";
$code = file_get_html($cnx);

$pp = $code->find('div[id=price-panel]', 0)->childNodes(0);
$cnx_price = $pp->childNodes(0)->childNodes(0)->innertext;
$cnx_change = $pp->childNodes(1)->childNodes(0)->childNodes(0)->innertext;
$cnx_per = $pp->childNodes(1)->childNodes(0)->childNodes(1)->innertext;

$sen = "http://www.google.com/finance?q=INDEXBOM%3ASENSEX&ei=XoKiVLnYFI_rkAXZ2ICgBg";
$codes = file_get_html($sen);

$pp1 = $codes->find('div[id=price-panel]', 0)->childNodes(0);
$sen_price = $pp1->childNodes(0)->childNodes(0)->innertext;
$sen_change = $pp1->childNodes(1)->childNodes(0)->childNodes(0)->innertext;
$sen_per = $pp1->childNodes(1)->childNodes(0)->childNodes(1)->innertext;

$sensex = array("sensex" => array());
$sm[] = array('price'=>$sen_price, 'change'=>$sen_change, 'perc'=>$sen_per);
$sensex['sensex'] = $sm;
$nifty = array("nifty" => array());
$nm[] = array('price'=>$cnx_price, 'change'=>$cnx_change, 'perc'=>$cnx_per);
$nifty['nifty'] = $nm;

echo json_encode(array_merge($g,$l,$lb,$gb,$nifty,$sensex));

?>