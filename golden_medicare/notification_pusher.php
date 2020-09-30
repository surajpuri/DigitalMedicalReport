<?php

	$raw_data = file_get_contents("php://input");
	$report_data = json_decode($raw_data, false);
	$report_id = $report_data[0]->report_id;
	$report_title = $report_data[0]->report_title;
	$report_summary = $report_data[0]->report_summary;
	$report_remarks = $report_data[0]->report_remarks;
	$report_date = $report_data[0]->report_data;
	$token = $report_data[0]->token;
	$registration_ids = array();
	array_push($registration_ids, $token);

	$path_to_fcm = 'https://fcm.googleapis.com/fcm/send';
	$server_key = "AAAA2xzL-jw:APA91bEK4i3HMDmj0hjIgHEmK44aQX2wPYvbia2CXY36lC7G_PQgtPzt9YCZankeG497Q_5O-H2Uu6QONJxktI_vAy6J45kI7RSRzj1h-0pi_GTn1a4Xbzt7IM7yjoCMcOTT2Yp4au_9";

	$headers = array('Authorization:key=' .$server_key,'Content-Type:application/json');

	$fields = array('registration_ids'=>$registration_ids, 'data'=>array('report_id'=>$report_id, 'report_title'=>$report_title, 'report_summary'=>$report_summary, 'report_remarks'=>$report_remarks, 'report_date'=>$report_date));

	$payload = json_encode($fields);

	$curl_session = curl_init();
	curl_setopt($curl_session, CURLOPT_URL, $path_to_fcm);
	curl_setopt($curl_session, CURLOPT_POST, true);
	curl_setopt($curl_session, CURLOPT_HTTPHEADER, $headers);
	curl_setopt($curl_session, CURLOPT_RETURNTRANSFER, true);
	curl_setopt($curl_session, CURLOPT_SSL_VERIFYPEER, false);
	curl_setopt($curl_session, CURLOPT_IPRESOLVE,CURL_IPRESOLVE_V4);
	curl_setopt($curl_session, CURLOPT_POSTFIELDS, $payload);

echo("start");
	$result = curl_exec($curl_session);
	var_dump($result);


	curl_close($curl_session);

echo "end";
?>