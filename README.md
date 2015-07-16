# freefrog

Exquisite Organization -- For Free

## Information

This project is being integrated using
[Travis-CI](https://travis-ci.org/).

[![Build Status](https://travis-ci.org/couragelabs/freefrog.svg?branch=master)](https://travis-ci.org/couragelabs/freefrog)

[![Dependencies Status](http://jarkeeper.com/couragelabs/freefrog/status.svg)](http://jarkeeper.com/couragelabs/freefrog)

Artifacts generated:

 * [API Documentation](http://s3.amazonaws.com/freefrog/docs/uberdoc.html)
 * [Specs](http://s3.amazonaws.com/freefrog/docs/specs.txt)

## Development

### Getting Started

Note: we all use Macs, so some commands in here (like "open") may not work
on our linux machine unless you've aliased them. 

Also, you'll need [Homebrew](http://brew.sh/) so you can:

    brew install gnu-sed

### Contributing

The maintainers accept pull requests gratefully. You can submit whatever you 
are inspired to submit and we promise to:

 * Accept without changes, or
 * Tell you what we would need in order to accept it.

If you contribute enough we will probably invite you to become a committer.

If you contribute even more we may invite you to become a partner in Courage Labs.

If you'd like to see a list that the maintainers believe we should build next,
you can visit [our Trello board](https://trello.com/b/NwocOwAv/freefrog). Don't
feel limited by that list. We could be wrong!

**Before submitting a pull request** run:

    ./full-build

This will run the same stuff our continuous integration server runs. This way, 
you can know if you broke the build BEFORE your code makes it into master,
keeping everything nice and clean.

What to do if ancient.txt is not empty:

    lein ancient upgrade :no-tests && git add project.clj && ./full-build

### Various commands

Running the specs once (SLOW):

    lein spec

Or autotest (SLOW to start, then FAST to develop):

    lein autotest

To see what the documentation looks like:

    lein docs && open docs/uberdoc.html

## Running it locally (COMING SOON -- once we make this a real app again)

    lein freefrog

## Operating using Docker (COMING SOON)

Docker is our preferred approach for operating Freefrog. An example file
can be found in the root:

    ./run-with-docker

## Getting Help

If the information above isn't telling you what you need to know, come chat
with us! We hang out in
[our IRC Channel](https://kiwiirc.com/client/irc.freenode.net/?nick=guest|?#couragelabs)
pretty regularly.

## License

Copyright © 2015 Courage Labs

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
