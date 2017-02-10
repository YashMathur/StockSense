<?php
    include('simple_html_dom.php');
    $f = "http://www.moneycontrol.com/stocks/marketstats/nsegainer/";
    $s = "http://www.moneycontrol.com/stocks/marketstats/nseloser/";
    $dom1 = file_get_html($f);
    $arr = array("data" => array());
    $g = array("gain" => array());
    $l = array("lose" => array());
    $gb = array("gainb" => array());
    $lb = array("loseb" => array());

    $number = count($dom1->find('.bsr_table table tr'));
    if($number>11){
    	$number = 11;
    }
    for($i=1;$i<$number;$i++){
        $n = $dom1->find('.bsr_table table', 0)->childNodes(1)->childNodes($i)->childNodes(0)->childNodes(0)->childNodes(0)->find('a', 0)->plaintext;
        $ltp =$dom1->find('.bsr_table table ', 0)->childNodes(1)->childNodes($i)->childNodes(6)->plaintext;
        $change = $dom1->find('.bsr_table table ', 0)->childNodes(1)->childNodes($i)->childNodes(8)->plaintext;
        $knse[] = array('symbol' =>$n, 'ltp' => $ltp, 'netPrice' => $change);
    }
    $dom1->clear();
    $g['gain'] = $knse;

    //echo json_encode(array_merge($g,$l,$lb,$gb,$nifty,$sensex));
    //echo json_encode($g);
    $dom1 = "";
    $dom1 = file_get_html($s);
    $number = count($dom1->find('.bsr_table table tr'));
    if($number>10){
    	$number = 10;
    }
    for($i=1;$i<$number;$i++){
        $n = $dom1->find('.bsr_table table', 0)->childNodes(1)->childNodes($i)->childNodes(0)->childNodes(0)->childNodes(0)->find('a', 0)->plaintext;
        $ltp =$dom1->find('.bsr_table table ', 0)->childNodes(1)->childNodes($i)->childNodes(6)->plaintext;
        $change = $dom1->find('.bsr_table table ', 0)->childNodes(1)->childNodes($i)->childNodes(8)->plaintext;
        $knsel[] = array('symbol' =>$n, 'ltp' => $ltp, 'netPrice' => $change);
    }
    //$dom1->clear();
    $g['gain'] = $knse;
    $l['lose'] = $knsel;
    $w = "http://www.moneycontrol.com/stocks/marketstats/bsegainer/";
    $dom1 = file_get_html($w);
    $number = count($dom1->find('.bsr_table table tr'));
    if($number>10){
    	$number = 10;
    }
    for($i=1;$i<$number;$i++){
        $n = $dom1->find('.bsr_table table', 0)->childNodes(1)->childNodes($i)->childNodes(0)->childNodes(0)->find('a', 0)->plaintext;
        $ltp =$dom1->find('.bsr_table table ', 0)->childNodes(1)->childNodes($i)->childNodes(6)->plaintext;
        $change = $dom1->find('.bsr_table table ', 0)->childNodes(1)->childNodes($i)->childNodes(8)->plaintext;
        $kb[] = array('symbol' =>$n, 'ltp' => $ltp, 'netPrice' => $change);
    }
    $gb['gainb'] = $kb;
    $w = "http://www.moneycontrol.com/stocks/marketstats/bseloser/";
    $dom1 = file_get_html($w);
    $number = count($dom1->find('.bsr_table table tr'));
    if($number>10){
    	$number = 10;
    }
    for($i=1;$i<$number;$i++){
        $n = $dom1->find('.bsr_table table', 0)->childNodes(1)->childNodes($i)->childNodes(0)->childNodes(0)->find('a', 0)->plaintext;
        $ltp =$dom1->find('.bsr_table table ', 0)->childNodes(1)->childNodes($i)->childNodes(6)->plaintext;
        $change = $dom1->find('.bsr_table table ', 0)->childNodes(1)->childNodes($i)->childNodes(8)->plaintext;
        $mb[] = array('symbol' =>$n, 'ltp' => $ltp, 'netPrice' => $change);
    }
    $dom1 = "" ;
    $lb['loseb'] = $mb;

    $cnx = "http://www.google.com/finance?q=NSE%3ANIFTY&ei=VoKiVIigLMSxkAWZsYGwCA";
    $dom1 = file_get_html($cnx);
    $pp = $dom1->find('div[id=price-panel]', 0)->childNodes(0);
    $cnx_price = $pp->childNodes(0)->childNodes(0)->innertext;
    $cnx_change = $pp->childNodes(1)->childNodes(0)->childNodes(0)->innertext;
    $cnx_per = $pp->childNodes(1)->childNodes(0)->childNodes(1)->innertext;
    $dom1 = "";

    $sen = "http://www.google.com/finance?q=INDEXBOM%3ASENSEX&ei=XoKiVLnYFI_rkAXZ2ICgBg";
    $dom1 = file_get_html($sen);
    $pp = $dom1->find('div[id=price-panel]', 0)->childNodes(0);
    $sen_price = $pp->childNodes(0)->childNodes(0)->innertext;
    $sen_change = $pp->childNodes(1)->childNodes(0)->childNodes(0)->innertext;
    $sen_per = $pp->childNodes(1)->childNodes(0)->childNodes(1)->innertext;
    $dom1="";
    $sensex = array("sensex" => array());
    $sm[] = array('price'=>$sen_price, 'change'=>$sen_change, 'perc'=>$sen_per);
    $sensex['sensex'] = $sm;
    $nifty = array("nifty" => array());
    $nm[] = array('price'=>$cnx_price, 'change'=>$cnx_change, 'perc'=>$cnx_per);
    $nifty['nifty'] = $nm;
    echo json_encode(array_merge($g,$l,$lb,$gb,$nifty,$sensex));
?>
