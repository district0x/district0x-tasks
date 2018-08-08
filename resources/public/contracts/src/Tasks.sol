pragma solidity ^0.4.24;

import "./Ownable.sol";

contract Tasks is Ownable {
    event LogAddTask (
        uint indexed id,
        string title,
        bool is_active,
        uint bidding_ends_on
    );
    
    event LogUpdateActive(uint indexed id, bool is_active);
    
    event LogUpdateBiddingEndsOn(uint indexed id, uint bidding_ends_on);


    modifier validBiddingEndsOn(uint bidding_ends_on) {
        require(bidding_ends_on <= block.timestamp, "Timestamp has to be in the future");
        _;
    }

    modifier validTitle(string title) {
        require(bytes(title).length > 0, "Title of the task is required");
        _;
    }


  /**
   * @notice Create constant random unique _id for task.
   * The reason is to not use storage, because events are cheaper.
   * Use _id to update tasks by events.
   */
    function addTask(string _title, bool _is_active, uint _bidding_ends_on)
    public
    onlyOwner
    validTitle(_title)
    validBiddingEndsOn(_bidding_ends_on)
    {
        uint _id = uint(keccak256(block.timestamp, _bidding_ends_on));
        emit LogAddTask(
            _id,
            _title,
            _is_active,
            _bidding_ends_on
        );
    }

    function updateActive(uint _id, bool _is_active) public onlyOwner {
        emit LogUpdateActive(_id, _is_active);
    }

    function updateBiddingEndsOn(uint _id, uint _bidding_ends_on)
    public
    onlyOwner
    validBiddingEndsOn(_bidding_ends_on)
    {
        emit LogUpdateBiddingEndsOn(_id, _bidding_ends_on);
    }
}