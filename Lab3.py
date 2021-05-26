from copy import deepcopy
from re import finditer
from itertools import combinations

def delete_epsilon(derivations):
    derivationsClone = deepcopy(derivations)
    characters = [1]
    while characters:
        derivations = deepcopy(derivationsClone)
        characters = []
        for (key, value) in derivations.items():
            if '' in value:
                characters.append(key)
        for letter in characters:
            if len(derivations[letter]) == 1:
                non.remove(letter)
                for key, value in derivations.items():
                    for i, v in enumerate(value):
                        if letter in v:
                            derivationsClone[key][i] = v.replace(letter, "")
                del derivationsClone[letter]
            else:
                for key, value in derivations.items():
                    for v in value:
                        if letter in v:
                            pos = [m.start() for m in finditer(letter, v)]
                            to_remove = []

                            for i in range(1, len(pos)+1):
                                for tupple in list(combinations(pos, i)):
                                    to_remove.append(list(tupple))

                            for indexes in to_remove:
                                v_copy = v[:]

                                for index in indexes:
                                    v_copy = v_copy[:index] + " " + v_copy[index+1:]
                                v_copy = v_copy.replace(" ", "")

                                if v_copy not in derivationsClone[key]:
                                    derivationsClone[key].append(v_copy.replace(" ", ""))
                derivationsClone[letter].remove('')

    return derivationsClone


def delete_renamings(derivations):
    characters = [1]
    while characters:
        characters = []
        for key, value in derivations.items():
            for v in value:
                if len(v) == 1 and v in derivations.keys():
                    characters.append((key, v))
        for tuppl in characters:
            for rule in derivations[tuppl[1]]:
                if rule not in derivations[tuppl[0]]:
                    derivations[tuppl[0]].append(rule)
            derivations[tuppl[0]].remove(tuppl[1])
    return derivations


def delete_nonproductive(derivations):
    ending = []
    for key, value in derivations.items():
        found = False
        for v in value:
            if not any(letter in v for letter in derivations.keys()):
                found = True
        if found:
            ending.append(key)

    found = True
    non_ending = []
    while found:
        found = False
        non_ending = list(set(derivations.keys()) - set(ending))
        for n in non_ending:
            for v in derivations[n]:
                if any(letter in v for letter in ending):
                    found = True
                    ending.append(n)
                    break
    derivationsClone = deepcopy(derivations)
    for n in non_ending:
        for key, value in derivations.items():
            if n == key:
                del derivationsClone[key]
                non.remove(key)
                continue
            for v in value:
                if n in v:
                    derivationsClone[key].remove(v)
    return derivationsClone


def delete_inaccesible(derivations):
    start = list(derivations.keys())[0]
    accesible = [start]

    for letter in accesible:
        for v in derivations[letter]:
            for n in derivations.keys():
                if n in v and n not in accesible:
                    accesible.append(n)
    derivationsClone = deepcopy(derivations)
    for key in derivationsClone.keys():
        if key not in accesible:
            del derivations[key]
    return derivations


def split_t(string):
    splitted = []
    old_s = ''
    for s in string:
        if s.isdigit():
           old_s += s
        else:
            splitted.append(old_s)
            old_s = s
    splitted.remove('')
    splitted.append(old_s)
    return splitted


def find_by_value(rule, value):
    for key, v in rule.items():
        if v == value:
            return key
    return None


def delete_variables(derivations):
    X = 'Y'
    new_derivations = {}
    # Remove derivations with more than 2 variables
    changes = True
    while changes:
        changes = False
        derivationsClone = deepcopy(derivations)
        for key, value in derivationsClone.items():
            for i, v in enumerate(value):
                variables = split_t(v)
                if len(variables) > 2:
                    changes = True

                    new_X = X + str(len(new_derivations)+1)
                    to_replace = "".join(variables[1:])
                    existing = find_by_value(new_derivations, to_replace)
                    if existing:
                        derivations[key][i] = variables[0] + existing
                        if key in new_derivations.keys():
                            new_derivations[key] = variables[0] + existing
                    else:
                        new_derivations[new_X] = to_replace
                        derivations[key][i] = variables[0] + new_X
                        if key in new_derivations.keys():
                            new_derivations[key] = variables[0] + new_X
        for key, value in new_derivations.items():
            derivations[key] = [value]
    return derivations

def delete_terminals(derivations):
    # Remove non single terminal symbols
    X = 'X'
    new_derivations = {}
    for key, value in derivations.items():
        for i, v in enumerate(value):
            variables = split_t(v)
            if len(variables) == 2:
                for letter in variables:
                    if letter not in derivations.keys():
                        existing = find_by_value(new_derivations, letter)
                        if existing:
                            derivations[key][i] = derivations[key][i].replace(letter, existing)
                        else:
                            new_X = X + str(len(new_derivations)+1)
                            new_derivations[new_X] = letter
                            derivations[key][i] = derivations[key][i].replace(letter, new_X)
    for key, value in new_derivations.items():
        derivations[key] = [value]
    return derivations


def display(derivations):
    for key, value in derivations.items():
        print(key + " -> " + value[0], end=' ')
        for v in value[1:]:
            print("| " + v, end=' ')
        print()


derivations = {}
non = []
term = []

line = input()
while line:
    non.append(line.split(" ")[0])
    if len(line.split(" ")) == 1:
        term.append("")
    else:
        term.append(line.split(" ")[1])
    line = input()

for n in non:
    derivations[n] = []

for i,n in enumerate(non):
    derivations[n].append(term[i])

display(derivations)

derivations = delete_epsilon(derivations)

print("\nEliminating e")
display(derivations)

derivations = delete_renamings(derivations)

print("\nEliminating unit productions")
display(derivations)

derivations = delete_nonproductive(derivations)

print("\nEliminating nonproductive")
display(derivations)

derivations = delete_inaccesible(derivations)

print("\nEliminating inaccesible")
display(derivations)

derivations = delete_variables(derivations)

print("\nAfter removing more than 2 variables")
display(derivations)

derivations = delete_terminals(derivations)
print("\nChomsky Normal Form")
display(derivations)