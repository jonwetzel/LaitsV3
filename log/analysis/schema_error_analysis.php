<?php
	include "problemObject.php";
	$checkValidity = false;
	$updateType = "equal";
	$updateType = "oneSkill";
	$updateType = "minimum";
	$updateType = "ratio";
	$updateType = "problem";
	//$updateType = "nodeType";
	//$updateType = "nodeTypePerProblem";
	main();
	function analyzeLogs(){
		$section = "";
		$prob = array(/*"rabbits-sum15",*/ "111", "107", "CPI-2014-ps2-01", "isle3-sum15-study", "retirement-sum15","CPI-2014-ps2-02","CPI-2014-ps2-04","CPI-2014-ps3-03","CPI-2014-ps4-02", "CPI-2014-ps3-07", "CPI-2014-ps5-04");
		//$prob = array("CPI-2014-ps5-04");

		$str = '(';
		foreach($prob as $p)
			$str .= "'".$p."', ";
		$problemString = substr($str, 0, -2).")";

		//database variables
		$user = "root";
		$password = "qwerty211";
		$db_name = "laits_experiments";

		$mysqli = mysqli_connect("localhost", $user, $password, $db_name);
		$query = <<<EOT
		SELECT t1.user, t1.problem, t1.session_id, t2.method, t2.message FROM (SELECT user, problem, session_id, time FROM session WHERE section = "$section" AND mode = "STUDENT" AND problem IN $problemString /*AND user = "ZimeiHuo"*/) AS t1 JOIN (SELECT tid, method, message, session_id FROM step WHERE method = "solution-step") as t2 USING (session_id) ORDER BY user ASC, time ASC, problem ASC, tid ASC;
EOT;
		//echo $query;

		$result = mysqli_query($mysqli, $query);
		$first = true;
		$cp = null;
		$currentUser = null;
		$users = array();
		$oldRow = null;
		$olderRow = null;
		$currentNode = null;
		$count = 0;
		$timeArray = array();
		$oldMessage = null;
		while($row = $result->fetch_assoc()){
			$message = json_decode($row["message"], true);
			if($first){
				$first = false;
				//$currentUser = new User($row["user"]);
				$cp = new Problem($row["problem"]);
				$oldRow = $row;
				$olderRow = $row;
				$oldMessage = $message;
			}
			//echo json_encode($row)."<br/>";
			if($message['type'] === "solution-check"){
				//check for new user
				if($currentUser == null || $currentUser->name != $row['user']){
					if($currentNode != null)
						$currentUser->pushNode($currentNode);
					if($currentUser != null){
						normalize($currentUser, $oldRow['problem']);
						array_push($users, $currentUser);
					}
					$currentUser = new User($row["user"]);
					//echo json_encode($currentUser);
					array_push($currentUser->problemNames, $row["problem"]);
					$currentUser->total = 0;
					$currentUser->incorrect = 0;
					$count = 0;
					$timeArray = array();
					$cp = new Problem($row["problem"]);
				} else if($oldRow['problem'] != $row['problem']){
					normalize($currentUser, $oldRow['problem']);
					$cp = new Problem($row["problem"]);
					$currentUser->currentProblemNodes = 0;
					resetSchemas($currentUser);
					if(!in_array($row["problem"], $currentUser->problemNames)){
						array_push($currentUser->problemNames, $row["problem"]);
					}
				}

				//check for a new node and create it
				if($currentNode == null){
					$currentNode = createNode($cp, $currentUser, $row);
				} else if($currentNode->name != $message['node']){
					$currentUser->pushNode($currentNode);
					$tempNode = $currentUser->getNode($message['node']);
					if($tempNode != null){
						$currentNode = $tempNode;
					} else {
						$currentNode = createNode($cp, $currentUser, $row);
					}
				}

				$currentUser->total++;
				if(array_key_exists("checkResult", $message) && $message["checkResult"] == "CORRECT"){
					if($count > 0){
						array_push($timeArray, $message["time"]);
						if(isValid($currentUser, $count, $timeArray))
							updateCount($currentUser, $currentNode, $count);
						$count = 0;
						$timeArray = array();
					}
					$checkNext = false;
				} else if(array_key_exists("checkResult", $message) && $message["checkResult"] == "INCORRECT"){
					$currentUser->incorrect++;
					if(!in_array($oldMessage['time'], $timeArray))
						array_push($timeArray, $oldMessage['time']);

					if(!in_array($message['time'], $timeArray))
						array_push($timeArray, $message['time']);
					if($checkNext){
						if(isValid($currentUser, $count, $timeArray))
							updateCount($currentUser, $currentNode, $count);
						$count = 0;
						$timeArray = array($oldMessage['time'], $message['time']);
					}
					if($message["solutionProvided"] == "true")
						$checkNext = true;
					$count++;
				}
			} else if($message["type"] === "self-referencing-accumulator" || $message["type"] === "self-referencing-function"){
				updateCount($currentUser, $currentNode, 1);
			}

			$oldRow = $row;
			$oldMessage = $message;
		}
		array_push($users, $currentUser);
		echo (json_encode($users));
		return $users;
	}

	function createNode($cp, $cu, $row){
		$m = json_decode($row["message"], true);
		$n = new Node($m["node"]);
		$n->problem = $row["problem"];
		$n->schemas = $cp->getSchemaMap($m["node"]);
		$n->schemaNames = $cp->getSchemasForNode($m["node"]);
		$n->setSchemaName();
		$cu->currentProblemNodes++;
		//$n->setSkills();
		$n->type = $cp->getNodeType($m["node"]);
		setCurrentIndexes($cu, $n);
		//$n->difficultyParams = $cp->getDifficultyParams($m["node"]);
		//$n->setSkills();

		return $n;
	}

	function updateCount(/* user */ $cu, /* node */ $n, /* number */ $c){
		$userSchema = null;
		if(count($n->schemaNames) < 1)
			return ;
		switch($GLOBALS["updateType"]){
			case "equal":
				foreach($n->schemas as $schemaID => $schema){
					$userSchema = $cu->getSchema($schema);
					if(!array_key_exists($schemaID, $userSchema->currentProblemIndexes)){
						setCurrentIndexes($cu, $n);
						$userSchema = $cu->getSchema($schema);
					}
					$userSchema->errorCounts[$userSchema->currentProblemIndexes[$schemaID]] += $c;
					$cu->pushSchema($userSchema);
				}
				break;
			case "oneSkill":
				$ID = (count($n->schemaNames) == 1 ? key($n->schemas) : $n->name);
				$userSchema = $cu->getSchema($n->schemaName);
				if(!array_key_exists($ID, $userSchema->currentProblemIndexes)){
					setCurrentIndexes($cu, $n);
					$userSchema = $cu->getSchema($n->schemaName);
				}
				$userSchema->errorCounts[$userSchema->currentProblemIndexes[$ID]] += $c;
				$cu->pushSchema($userSchema);
				break;
			case "ratio":
				$counts = array();
				$numberSchemas = count($n->schemas);
				foreach($n->schemas as $schemaID => $schema){
					$userSchema = $cu->getSchema($schema);
					if(!array_key_exists($schemaID, $userSchema->currentProblemIndexes)){
						setCurrentIndexes($cu, $n);
						$userSchema = $cu->getSchema($schema);
					}
					$counts[$schemaID] = count($userSchema->errorCounts);
					if($numberSchemas == 1)
						$userSchema->errorCounts[$userSchema->currentProblemIndexes[$schemaID]] += $c;
				}
				if($numberSchemas > 1){
					$sum = ($numberSchemas - 1) * array_sum($counts);
					foreach($n->schemas as $schemaID => $schema){
						$userSchema = $cu->getSchema($schema);
						$userSchema->errorCounts[$userSchema->currentProblemIndexes[$schemaID]] += ($c*(($sum - $counts[$schemaID])/$sum));
						$cu->pushSchema($userSchema);
					}
				}
				break;
			case "minimum":
				$counts = array();
				foreach($n->schemas as $schemaID => $schema){
					$userSchema = $cu->getSchema($schema);
					if(!array_key_exists($schemaID, $userSchema->currentProblemIndexes)){
						setCurrentIndexes($cu, $n);
						$userSchema = $cu->getSchema($schema);
					}
					$counts[$schemaID] = count($userSchema->errorCounts);
				}
				$min = array_keys($counts, min($counts));
				$count = count($min);
				if($count > 0){
					foreach($min as $m){
						$minSchema = $cu->getSchema($n->schemas[$m]);
						$minSchema->errorCounts[$minSchema->currentProblemIndexes[$m]] += $c/$count;
						$cu->pushSchema($minSchema);
					}
				}
				break;
			case "problem":
				$end = end($cu->problemNames);
				$userSchema = $cu->getSchema("problems");
				$userSchema->errorCounts[$userSchema->currentProblemIndexes[$end]] += $c;
				$cu->pushSchema($userSchema);
				break;
			case "nodeType":
				$userSchema = $cu->getSchema($n->type);
				$userSchema->errorCounts[$userSchema->currentProblemIndexes[$n->name]] += $c;
				$cu->pushSchema($userSchema);
				break;
			case "nodeTypePerProblem":
				$userSchema = $cu->getSchema($n->type);
				$end = end($cu->problemNames);
				$userSchema->errorCounts[$userSchema->currentProblemIndexes[$end]] += $c;
				$cu->pushSchema($userSchema);
				break;
		}
	}

	function setCurrentIndexes($cu, $n){
		if(count($n->schemaNames) == 0)
			return ;

		switch($GLOBALS["updateType"]){
			case "equal":
			case "ratio":
			case "minimum":
				foreach($n->schemas as $schemaID => $schema){
					$userSchema = $cu->getSchema($schema);
					if(!array_key_exists($schemaID, $userSchema->currentProblemIndexes)){
						$count = count($userSchema->errorCounts);
						$userSchema->errorCounts[$count] = 0;
						$userSchema->currentProblemIndexes[$schemaID] = $count;
					}
					$cu->pushSchema($userSchema);
				}
				break;
			case "oneSkill":
				$ID = (count($n->schemaNames) == 1 ? key($n->schemas) : $n->name);
				$userSchema = $cu->getSchema($n->schemaName);
				if(!array_key_exists($ID, $userSchema->currentProblemIndexes)){
					$count = count($userSchema->errorCounts);
					$userSchema->errorCounts[$count] = 0;
					$userSchema->currentProblemIndexes[$ID] = $count;
				}
				$cu->pushSchema($userSchema);
				break;
			case "problem":
				$userSchema = $cu->getSchema("problems");
				$end = end($cu->problemNames);
				if(!array_key_exists($end, $userSchema->currentProblemIndexes)){
					$count = count($userSchema->errorCounts);
					$userSchema->errorCounts[$count] = 0;
					$userSchema->currentProblemIndexes[$end] = $count;
				}
				$cu->pushSchema($userSchema);
				break;
			case "nodeType":
				$userSchema = $cu->getSchema($n->type);
				if(!array_key_exists($n->name, $userSchema->currentProblemIndexes)){
					$count = count($userSchema->errorCounts);
					$userSchema->errorCounts[$count] = 0;
					$userSchema->currentProblemIndexes[$n->name] = $count;
				}
				$cu->pushSchema($userSchema);
				break;
			case "nodeTypePerProblem":
				$userSchema = $cu->getSchema($n->type);
				$end = end($cu->problemNames);
				if(!array_key_exists($end, $userSchema->currentProblemIndexes)){
					$count = count($userSchema->errorCounts);
					$userSchema->errorCounts[$count] = 0;
					$userSchema->currentProblemIndexes[$end] = $count;
				}
				$cu->pushSchema($userSchema);
				break;
		}
	}

	function isValid(/* user */ $cu, /* number */$c, /* array */ $ta){
		if(!$GLOBALS["checkValidity"]){
			return true;
		}

		$ratio = $cu->incorrect/$cu->total;
		$isValid = true;
		$minTimeDiff = 7;
		if($ratio >= 0.4){
			//has a high chance of gaming
			if($c >= 2)
				$isValid = false;
		} else {
			//could have gamed just this one.
			// here next step time becomes important
			$count = count($ta);
			if($count > 3){
				//previous time, first error time, second error time, next time - there has to be four for a yellow
				$diff = $ta[$count - 1] - $ta[$count - 2];
				if($diff < $minTimeDiff){
					// there was a yellow scenario and the person moved on the next one
					$isValid = false;
				} else {
					if(($ta[1] - $ta[0]) <= $minTimeDiff){
						$isValid = false;
					}
				}
			}
		}

		return $isValid;
	}

	function resetSchemas($cu){
		foreach($cu->schemas as $schema){
			$schema->currentProblemIndexes = array();
			$cu->pushSchema($schema);
		}
	}

	function normalize($cu, $problem){
		$count = ($GLOBALS["updateType"] == "problem" ? $cu->currentProblemNodes : array());
		switch($GLOBALS["updateType"]){
			case "problem":
				$userSchema = $cu->getSchema("problems");
				$userSchema->errorCounts[$userSchema->currentProblemIndexes[$problem]]/$count;
				$cu->pushSchema($userSchema);
				break;
			case "nodeTypePerProblem":
				$nodes = $cu->pNodes;
				foreach($nodes as $node){
					$type = $node->type;
					if($node->problem == $problem){
						if(!array_key_exists($type, $count))
							$count[$type] = 0;
						$count[$type]++;
					}
				}
				foreach($count as $type => $c){
					$userSchema = $cu->getSchema($type);
					$userSchema->errorCounts[$userSchema->currentProblemIndexes[$problem]]/$c;
					$cu->pushSchema($userSchema);
				}
				break;
			default:
				break;

		}
	}

	function writeData($users){
		$schemas = array();
		$tab = "\t";
		$nextLine = "\n";
		foreach($users as $user){
			foreach($user->schemas as $schema){
				if(!array_key_exists($schema->name, $schemas))
					$schemas[$schema->name] = "";
				$str = $user->codeName.$tab;
				foreach($schema->errorCounts as $c){
					$str .= $c.$tab;
				}
				$str .= $nextLine;

				$schemas[$schema->name] .= $str;
			}
		}
		
		foreach($schemas as $schemaName => $schemaData){
			$name = $schemaName."_".$GLOBALS["updateType"].".xls";
			$file = fopen($name, "w");
			fwrite($file, $schemaData);
			fclose($file);
		}
	}

	function main(){
		$objs = analyzeLogs();
		writeData($objs);
	}
?>
