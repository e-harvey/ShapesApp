<?php
	class Response {
		private $status;
		private $data;

		public function __construct($status = false, $data = array())
		{
			$this->setStatus($status);
			$this->setData($data);
		}

		public function setStatus($status)
		{
			$this->status = $status;
		}

		public function setData($data)
		{
			$this->data = $data;
		}

		public function send()
		{
			$response = array("status" => $this->status, "data" => $this->data);
			echo json_encode($response);
			return true;
		}
	}
?>
