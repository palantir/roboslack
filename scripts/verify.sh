#!/usr/bin/env bash

set -e
set -o pipefail

function join_by { local d=$1; shift; echo -n "$1"; shift; printf "%s" "${@/#/$d}"; }

cd "$(dirname "$0")"/..

declare -a env_arr=(
    "ROBOSLACK_TOKEN_TPART"
    "ROBOSLACK_TOKEN_BPART"
    "ROBOSLACK_TOKEN_XPART"
)

# Validate all environment vars
for env_var_name in "${env_arr[@]}"; do
    env_var_value="${!env_var_name}"
done

# Build gradle cli parameters string
inject_env_vars_string=''
for env_var_name in "${env_arr[@]}"; do
    env_var_value="${!env_var_name}"
    inject_env_vars_string="$inject_env_vars_string-D$env_var_name=$env_var_value "
done

new_lined_args=$(join_by $'\n' "${env_arr[@]}")
echo -e "Running './gradlew build check' with environment variables: \n${new_lined_args}\n";

./gradlew build check ${inject_env_vars_string} --parallel --stacktrace
