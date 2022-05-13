# Based on https://willhaley.com/blog/generate-jwt-with-bash/
# JWT Encoder Bash Script

accountToken=$DELEGATE_TOKEN
accountId=$ACCOUNT_ID
issuer=$ISSUER

  if [ -z "$accountToken" ]
  then
    echo "Missing DELEGATE_TOKEN in env"
    exit 1
  fi
  if [ -z "$accountId" ]
  then
    echo "Missing ACCOUNT_ID in env"
    exit 1
  fi
  if [ -z "$issuer" ]
  then
    echo "Missing ISSUER in env"
    exit 1
  fi
# Static header fields.
header='{
        "typ": "JWT",
        "alg":"HS256"
}'

payload=$( jq -n \
              --arg sub "$accountId" \
              --arg iss "$issuer" \
              '{sub: $sub, issuer: $iss}' )


# Use jq to set the dynamic `iat` and `exp`
# fields on the payload using the current time.
# `iat` is set to now, and `exp` is now + 60 seconds.
payload=$(
        echo "${payload}" | jq --arg time_str "$(date +%s)" \
        '
        ($time_str | tonumber) as $time_num
        | .iat=$time_num
        | .exp=($time_num + 120)
        '
)

base64_encode()
{
        declare input=${1:-$(</dev/stdin)}
        # Use `tr` to URL encode the output from base64.
        printf '%s' "${input}" | base64 | tr -d '=' | tr '/+' '_-' | tr -d '\n'
}

json() {
        declare input=${1:-$(</dev/stdin)}
        printf '%s' "${input}" | jq -c .
}

hmacsha256_sign()
{
        declare input=${1:-$(</dev/stdin)}
        printf '%s' "${input}" | openssl dgst -binary -sha256 -hmac "${secret}"
}

header_base64=$(echo "${header}" | json | base64_encode)
payload_base64=$(echo "${payload}" | json | base64_encode)

header_payload=$(echo "${header_base64}.${payload_base64}")
signature=$(echo "${header_payload}" | hmacsha256_sign | base64_encode)

JWT_TOKEN="${header_payload}.${signature}"

echo $JWT_TOKEN