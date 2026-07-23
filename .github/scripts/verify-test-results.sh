#!/usr/bin/env bash
set -euo pipefail

project_name="$(basename "$PWD")"
result_dir=""
result_files=()

candidate_dirs=(
  "build/test-results/test"
  "../../.gradle-build/$project_name/test-results/test"
  "../.gradle-build/$project_name/test-results/test"
)

for candidate_dir in "${candidate_dirs[@]}"; do
  if [[ -d "$candidate_dir" ]]; then
    mapfile -t result_files < <(find "$candidate_dir" -name "TEST-*.xml" -type f)
  fi

  if [[ "${#result_files[@]}" -gt 0 ]]; then
    result_dir="$candidate_dir"
    break
  fi
done

if [[ -z "$result_dir" ]]; then
  echo "Gradle 테스트 결과 XML 파일이 없습니다."
  printf '확인한 경로: %s\n' "${candidate_dirs[@]}"
  exit 1
fi

test_count="$(
  grep -ho 'tests="[0-9]*"' "${result_files[@]}" \
    | sed -E 's/tests="([0-9]*)"/\1/' \
    | awk '{ sum += $1 } END { print sum + 0 }'
)"

if [[ "$test_count" -le 0 ]]; then
  echo "실행된 테스트 수가 0개입니다."
  exit 1
fi

echo "실행된 테스트 수: $test_count"
