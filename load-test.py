from locust import HttpUser, task, between
from decimal import Decimal
import string
import logging
import random

def generate_string(length):
    characters = string.ascii_letters + string.digits
    return ''.join(random.choices(characters, k=length))

def generate_account_id():
    return random.randint(1000000, 9999999)

class User(HttpUser):

    @task(3) #high weigh to prevent starvation pool
    def create_transaction(self):
        """
        test createTransaction
        """
        from_account_id = generate_account_id()
        to_account_id = generate_account_id()
        transaction_no = generate_string(10)
        amount = str(round(random.uniform(0.01, 100), 2))
        description = generate_string(10)

        transaction_dto = {
            "fromAccountId": from_account_id,
            "toAccountId": to_account_id,
            "transactionNo": transaction_no,
            "amount": amount,
            "description": description
        }
        response = self.client.post("/api/transactions", json=transaction_dto)
        if response.status_code == 200:
            transaction_id = response.json().get("data").get("id")
            if transaction_id:
                self.existed_transaction_ids.append(transaction_id) # insert into pool
            else:
                logging.warning(f"create transaction failed with transaction_no: {transaction_no}")
        else:
            logging.warning(f"create transaction failed with status_code: {response.status_code}")

    @task(3)
    def delete_transaction(self):
        """
        test deleteTransaction
        """
        self.generate_transaction()
        if self.existed_transaction_ids:
            transaction_id = random.choice(self.existed_transaction_ids)
            self.client.delete(f"/api/transactions/{transaction_id}")
            self.existed_transaction_ids.remove(transaction_id)

    @task(3)
    def modify_transaction(self):
        """
        test modifyTransaction
        """
        self.generate_transaction()
        if self.existed_transaction_ids:
            transaction_id = random.choice(self.existed_transaction_ids)
            from_account_id = generate_account_id()
            to_account_id = generate_account_id()
            transaction_no = generate_string(5)
            amount = str(round(random.uniform(0.01, 100), 2))
            description = generate_string(10)

            transaction_modify_dto = {
                "id": transaction_id,
                "fromAccountId": from_account_id,
                "toAccountId": to_account_id,
                "transactionNo": transaction_no,
                "amount": amount,
                "description": description
            }
            # 发送 PUT 请求到修改交易的接口
            self.client.put(f"/api/transactions/{transaction_id}", json=transaction_modify_dto)

    @task(3)
    def list_all_transactions(self):
        """
        test listAllTransactions
        """
        self.generate_transaction()
        self.client.get("/api/transactions?page=1&size=10")

    @task(3)
    def get_transaction_by_id(self):
        """
        test getTransactionById
        """
        self.generate_transaction()
        if self.existed_transaction_ids:
            transaction_id = random.choice(self.existed_transaction_ids)
            self.client.get(f"/api/transactions/{transaction_id}")

    def on_start(self):
        self.existed_transaction_ids = []

    def generate_transaction(self):
        if len(self.existed_transaction_ids) < 5:
            self.create_transaction()

    wait_time = between(1, 5)