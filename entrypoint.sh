#!/bin/sh
# Decode wallet from base64 env var
if [ -n "$ORACLE_WALLET_BASE64" ]; then
  mkdir -p /app/wallet
  echo "$ORACLE_WALLET_BASE64" | base64 -d | tar xzf - -C /app/wallet
  echo "Wallet extracted to /app/wallet"
fi

exec java -jar /app/app.jar
