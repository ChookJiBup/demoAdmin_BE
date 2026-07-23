#!/usr/bin/env bash
set -euo pipefail

base_sha="${1:-}"

if [[ -z "$base_sha" || "$base_sha" =~ ^0+$ ]]; then
  base_sha="$(git rev-list --max-parents=0 HEAD)"
fi

changed_files="$(git diff --name-only "$base_sha" HEAD)"
main_changed=()
test_changed=()

while IFS= read -r file; do
  [[ -n "$file" ]] || continue

  if [[ "$file" =~ ^src/main/java/.*\.java$ ]]; then
    if [[ ! "$file" =~ Controller\.java$ ]]; then
      main_changed+=("$file")
    fi
  fi

  if [[ "$file" =~ ^src/test/java/.*Test\.java$ ]]; then
    test_changed+=("$file")
  fi
done <<< "$changed_files"

if [[ "${#main_changed[@]}" -eq 0 ]]; then
  echo "테스트 작성 확인 대상 운영 코드 변경이 없습니다."
  exit 0
fi

if [[ "${#test_changed[@]}" -eq 0 ]]; then
  echo "운영 Java 코드 변경이 있지만 테스트 파일 변경이 없습니다."
  printf '%s\n' "${main_changed[@]}"
  exit 1
fi

has_test_annotation=false
for file in "${test_changed[@]}"; do
  if [[ -f "$file" ]] \
      && grep -Eq '@(Test|ParameterizedTest|RepeatedTest|TestFactory|Nested)' "$file"; then
    has_test_annotation=true
    break
  fi
done

if [[ "$has_test_annotation" != true ]]; then
  echo "변경된 테스트 파일에서 테스트 어노테이션을 찾지 못했습니다."
  printf '%s\n' "${test_changed[@]}"
  exit 1
fi

echo "운영 코드 변경에 대한 테스트 파일 변경을 확인했습니다."
