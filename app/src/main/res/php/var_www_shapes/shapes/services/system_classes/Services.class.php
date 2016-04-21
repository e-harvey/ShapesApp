<?php
	class Services {
		public function serve() {
			$res = new Response();
			if (isset($_GET['service']) && isset($_GET['action'])) {
				$service_name = $_GET['service'];
				$method_name = $_GET['action'];
				global $PUBLIC_CLASS_PATH;
				if (file_exists($PUBLIC_CLASS_PATH.$service_name.'.class.php')) {
					if (class_exists($service_name)) {
						$service = new $service_name();
						if (method_exists($service, $method_name))
							$res = $service->$method_name();
						else
							$res = new Response(false, "The specified action does not exist.");
					}
				} else {
					$res = new Response(false, "The specified service does not exist.");
				}
			} else {
				$res = new Response(false, "The service name or action was not specified.");
			}

			$res->send();
		}
	}
?>
